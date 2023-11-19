package be.bluexin.mcui.api.scripting

import be.bluexin.mcui.Constants
import be.bluexin.mcui.config.ConfigHandler
import com.google.common.base.Strings
import com.google.common.io.PatternFilenameFilter
import li.cil.repack.com.naef.jnlua.LuaState
import li.cil.repack.com.naef.jnlua.LuaStateFiveFour
import li.cil.repack.com.naef.jnlua.LuaStateFiveThree
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.Channels
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.random.Random

/**
 * Ported from https://github.com/MightyPirates/OpenComputers/blob/2b40e8a5d0a899e5428a88f91455ed9b98af1606/src/main/scala/li/cil/oc/server/machine/luac/LuaStateFactory.scala
 */
abstract class JNLuaStateFactory {
    companion object {
        val isAvailable: Boolean
            get() {
                // Force initialization of all.
                val lua52 = Lua52.isAvailable
                val lua53 = Lua53.isAvailable
                val lua54 = Lua54.isAvailable
                return lua52 || lua53 || lua54
            }

        val luajRequested: Boolean get() = ConfigHandler.forceLuaJ || ConfigHandler.registerLuaJArchitecture

        val includeLuaJ: Boolean get() = !isAvailable || luajRequested

        val include52: Boolean get() = Lua52.isAvailable && !ConfigHandler.forceLuaJ

        val include53: Boolean get() = Lua53.isAvailable && ConfigHandler.enableLua53 && !ConfigHandler.forceLuaJ

        val include54: Boolean get() = Lua54.isAvailable && ConfigHandler.enableLua54 && !ConfigHandler.forceLuaJ

        val default53: Boolean get() = include53 && ConfigHandler.defaultLua53
    }

    object Lua52 : JNLuaStateFactory() {
        override val version: String get() = "52"

        override fun create(maxMemory: Int?): LuaState = if (maxMemory != null) LuaState(maxMemory) else LuaState()

        override fun openLibs(state: LuaState) {
            state.openLib(LuaState.Library.BASE)
            state.openLib(LuaState.Library.BIT32)
            state.openLib(LuaState.Library.COROUTINE)
            state.openLib(LuaState.Library.DEBUG)
            state.openLib(LuaState.Library.ERIS)
            state.openLib(LuaState.Library.MATH)
            state.openLib(LuaState.Library.STRING)
            state.openLib(LuaState.Library.TABLE)
            state.openLib(LuaState.Library.JAVA) // TODO : refine module to not give access to everything !
            state.pop(9)
        }
    }

    object Lua53 : JNLuaStateFactory() {
        override val version: String get() = "53"

        override fun create(maxMemory: Int?): LuaState =
            if (maxMemory != null) LuaStateFiveThree(maxMemory) else LuaStateFiveThree()

        override fun openLibs(state: LuaState) {
            state.openLib(LuaState.Library.BASE)
            state.openLib(LuaState.Library.COROUTINE)
            state.openLib(LuaState.Library.DEBUG)
            state.openLib(LuaState.Library.ERIS)
            state.openLib(LuaState.Library.MATH)
            state.openLib(LuaState.Library.STRING)
            state.openLib(LuaState.Library.TABLE)
            state.openLib(LuaState.Library.UTF8)
            state.openLib(LuaState.Library.JAVA) // TODO : refine module to not give access to everything !
            state.pop(9)
        }
    }

    object Lua54 : JNLuaStateFactory() {
        override val version: String get() = "54"

        override fun create(maxMemory: Int?): LuaState =
            if (maxMemory != null) LuaStateFiveFour(maxMemory) else LuaStateFiveFour()

        override fun openLibs(state: LuaState) {
            state.openLib(LuaState.Library.BASE)
            state.openLib(LuaState.Library.COROUTINE)
            state.openLib(LuaState.Library.DEBUG)
            state.openLib(LuaState.Library.ERIS)
            state.openLib(LuaState.Library.MATH)
            state.openLib(LuaState.Library.STRING)
            state.openLib(LuaState.Library.TABLE)
            state.openLib(LuaState.Library.UTF8)
            state.openLib(LuaState.Library.JAVA) // TODO : refine module to not give access to everything !
            state.pop(9)
        }
    }

    abstract val version: String

    // ----------------------------------------------------------------------- //
    // Initialization
    // ----------------------------------------------------------------------- //

    /** Set to true in initialization code below if available. */
    private var haveNativeLibrary = false

    private var currentLib = ""

    private val libraryName = run {
        val libExtension = run {
            if (SystemUtils.IS_OS_MAC) ".dylib"
            else if (SystemUtils.IS_OS_WINDOWS) ".dll"
            else ".so"
        }

        val platformName = run {
            if (!Strings.isNullOrEmpty(ConfigHandler.forceNativeLibPlatform)) ConfigHandler.forceNativeLibPlatform
            else {
                val systemName = run {
                    if (SystemUtils.IS_OS_FREE_BSD) "freebsd"
                    else if (SystemUtils.IS_OS_NET_BSD) "netbsd"
                    else if (SystemUtils.IS_OS_OPEN_BSD) "openbsd"
                    else if (SystemUtils.IS_OS_SOLARIS) "solari"
                    else if (SystemUtils.IS_OS_LINUX) "linux"
                    else if (SystemUtils.IS_OS_MAC) "darwin"
                    else if (SystemUtils.IS_OS_WINDOWS) "window"
                    else "unknown"
                }

                val archName = run {
                    if (Architecture.IS_OS_ARM64) "aarch64"
                    else if (Architecture.IS_OS_ARM) "arm"
                    else if (Architecture.IS_OS_X64) "x86_64"
                    else if (Architecture.IS_OS_X86) "x86"
                    else "unknown"
                }

                "$systemName-$archName"
            }
        }

        "libjnlua$version-$platformName$libExtension"
    }

    protected abstract fun create(maxMemory: Int? = null): LuaState

    protected abstract fun openLibs(state: LuaState)

    // ----------------------------------------------------------------------- //

    val isAvailable get() = haveNativeLibrary

    // Since we use native libraries we have to do some work. This includes
    // figuring out what we're running on, so that we can load the proper shared
    // libraries compiled for that system. It also means we have to unpack the
    // shared libraries somewhere so that we can load them, because we cannot
    // load them directly from a JAR. Lastly, we need to handle library overrides in
    // case the user wants to use custom libraries, or are not on a supported platform.
    @Suppress("UnstableApiUsage")
    fun init() {

        if (SystemUtils.IS_OS_WINDOWS && !ConfigHandler.alwaysTryNative) {
            if (SystemUtils.IS_OS_WINDOWS_XP) {
                Constants.LOG.warn("Sorry, but Windows XP isn't supported. I'm afraid you'll have to use a newer Windows. I very much recommend upgrading your Windows, anyway, since Microsoft has stopped supporting Windows XP in April 2014.")
                return
            }

            if (SystemUtils.IS_OS_WINDOWS_2003) {
                Constants.LOG.warn("Sorry, but Windows Server 2003 isn't supported. I'm afraid you'll have to use a newer Windows.")
                return
            }
        }

        lateinit var tmpLibFile: File
        if (!Strings.isNullOrEmpty(ConfigHandler.forceNativeLibPathFirst)) {
            val libraryTest = File(ConfigHandler.forceNativeLibPathFirst, libraryName);
            if (libraryTest.canRead()) {
                tmpLibFile = libraryTest
                currentLib = libraryTest.absolutePath
                Constants.LOG.info("Found forced-path filesystem library $currentLib.")
            } else
                Constants.LOG.warn("forceNativeLibPathFirst is set, but $currentLib was not found there. Falling back to checking the built-in libraries.")
        }

        if (currentLib.isEmpty()) {
            val libraryUrl = javaClass.getResource("/assets/opencomputers/lib/$libraryName")
            if (libraryUrl === null) {
                Constants.LOG.warn("Native library with name '$libraryName' not found.")
                return
            }

            val tmpLibName = "${Constants.MOD_ID}-${Constants.MOD_VERSION}-$version-$libraryName"
            val tmpBasePath = if (ConfigHandler.nativeInTmpDir) {
                val path = System.getProperty("java.io.tmpdir")
                when {
                    path == null -> ""
                    path.endsWith("/") || path.endsWith("\\") -> path
                    else -> "$path/"
                }
            } else "./"
            tmpLibFile = File(tmpBasePath + tmpLibName)

            // Clean up old library files when not in tmp dir.
            if (!ConfigHandler.nativeInTmpDir) {
                val libDir = File(tmpBasePath)
                if (libDir.isDirectory) {
                    for (file in libDir.listFiles(
                        PatternFilenameFilter(
                            "^" + Pattern.quote("${Constants.MOD_ID}-") + ".*" + Pattern.quote(
                                "-$libraryName"
                            ) + "$"
                        )
                    )!!) {
                        if (file.compareTo(tmpLibFile) != 0) {
                            file.delete()
                        }
                    }
                }
            }

            // If the file, already exists, make sure it's the same we need, if it's
            // not disable use of the natives.
            if (tmpLibFile.exists()) {
                var matching = true
                try {
                    val inCurrent = libraryUrl.openStream()
                    val inExisting = FileInputStream(tmpLibFile)
                    var inCurrentByte: Int
                    var inExistingByte: Int
                    do {
                        inCurrentByte = inCurrent.read()
                        inExistingByte = inExisting.read()
                        if (inCurrentByte != inExistingByte) {
                            matching = false
                            inCurrentByte = -1
                        }
                    } while (inCurrentByte != -1)
                    inCurrent.close()
                    inExisting.close()
                } catch (e: Throwable) {
                    matching = false
                }
                if (!matching) {
                    // Try to delete an old instance of the library, in case we have an update
                    // and deleteOnExit fails (which it regularly does on Windows it seems).
                    // Note that this should only ever be necessary for dev-builds, where the
                    // version number didn't change (since the version number is part of the name).
                    try {
                        tmpLibFile.delete()
                    } catch (_: Throwable) {
                        // Ignore.
                    }
                    if (tmpLibFile.exists()) {
                        Constants.LOG.warn("Could not update native library '${tmpLibFile.name}'!")
                    }
                }
            }

            // Copy the file contents to the temporary file.
            try {
                Channels.newChannel(libraryUrl.openStream()).use { inp ->
                    FileOutputStream(tmpLibFile).channel.use {
                        it.transferFrom(inp, 0, Long.MAX_VALUE)
                        tmpLibFile.deleteOnExit()
                        // Set file permissions more liberally for multi-user+instance servers.
                        tmpLibFile.setReadable(true, false)
                        tmpLibFile.setWritable(true, false)
                        tmpLibFile.setExecutable(true, false)
                    }
                    inp.close()
                }
            } catch (_: Throwable) {
                // Java (or Windows?) locks the library file when opening it, so any
                // further tries to update it while another instance is still running
                // will fail. We still want to try each time, since the files may have
                // been updated.
                // Alternatively, the file could not be opened for reading/writing.
                // Do nothing.
            }
            // Try to load the lib.
            currentLib = tmpLibFile.absolutePath
        }

        try {
            synchronized(this) {
                System.load(currentLib)
                create().close()
            }
            Constants.LOG.info("Found a compatible native library: '${tmpLibFile.name}'.")
            haveNativeLibrary = true
        } catch (t: Throwable) {
            if (ConfigHandler.logFullLibLoadErrors) {
                Constants.LOG.warn("Could not load native library '${tmpLibFile.name}'.", t)
            } else {
                Constants.LOG.trace("Could not load native library '${tmpLibFile.name}'.")
            }
            tmpLibFile.delete()
        }
    }

    init {
        init()

        if (!haveNativeLibrary) {
            Constants.LOG.warn("Platform doesn't support JNLua !")
        }
    }

    // ----------------------------------------------------------------------- //
    // Factory
    // ----------------------------------------------------------------------- //

    fun createState(): LuaState? {
        if (!haveNativeLibrary) return null

        try {
            val state = synchronized(this) {
                System.load(currentLib)
                if (ConfigHandler.limitMemory) create(Int.MAX_VALUE)
                else create()
            }
            try {
                // Load all libraries.
                openLibs(state)

                if (!ConfigHandler.disableLocaleChanging) {
                    state.openLib(LuaState.Library.OS)
                    state.getField(-1, "setlocale")
                    state.pushString("C")
                    state.call(1, 0)
                    state.pop(1)
                }

                // Prepare table for os stuff.
                state.newTable()
                state.setGlobal("os")

                // Kill compat entries.
                state.pushNil()
                state.setGlobal("unpack")
                state.pushNil()
                state.setGlobal("loadstring")
                state.getGlobal("math")
                state.pushNil()
                state.setField(-2, "log10")
                state.pop(1)
                state.getGlobal("table")
                state.pushNil()
                state.setField(-2, "maxn")
                state.pop(1)

                // Remove some other functions we don't need and are dangerous.
                state.pushNil()
                state.setGlobal("dofile")
                state.pushNil()
                state.setGlobal("loadfile")

                state.getGlobal("math")

                // We give each Lua state it's own randomizer, since otherwise they'd
                // use the good old rand() from C. Which can be terrible, and isn't
                // necessarily thread-safe.
                var random = Random(Random.nextLong())
                state.pushJavaFunction { lua ->
                    val r = random.nextDouble()
                    when (lua.top) {
                        0 -> lua.pushNumber(r)
                        1 -> {
                            val u = lua.checkNumber(1)
                            lua.checkArg(1, 1 <= u, "interval is empty")
                            lua.pushNumber(floor(r * u) + 1)
                        }

                        2 -> {
                            val l = lua.checkNumber(1)
                            val u = lua.checkNumber(2)
                            lua.checkArg(2, l <= u, "interval is empty")
                            lua.pushNumber(floor(r * (u - l + 1)) + l)
                        }

                        else -> throw IllegalArgumentException("wrong number of argument")
                    }
                    1
                }
                state.setField(-2, "random")

                state.pushJavaFunction { lua ->
                    random = Random(lua.checkInteger(1))
                    0
                }
                state.setField(-2, "randomseed")

                // Pop the math table.
                state.pop(1)

                return state
            } catch (t: Throwable) {
                Constants.LOG.warn("Failed creating Lua state.", t)
                state.close()
            }
        } catch (_: UnsatisfiedLinkError) {
            Constants.LOG.error("Failed loading the native libraries.")
        } catch (t: Throwable) {
            Constants.LOG.warn("Failed creating Lua state.", t)

        }
        return null
    }

    // Inspired by org.apache.commons.lang3.SystemUtils
    object Architecture {
        private val OS_ARCH = try {
            System.getProperty("os.arch")
        } catch (_: SecurityException) {
            null
        }

        val IS_OS_ARM = isOSArchMatch("arm")

        val IS_OS_ARM64 = isOSArchMatch("aarch64")

        val IS_OS_X86 = isOSArchMatch("x86") || isOSArchMatch("i386")

        val IS_OS_X64 = isOSArchMatch("x86_64") || isOSArchMatch("amd64")

        private fun isOSArchMatch(archPrefix: String): Boolean = OS_ARCH != null && OS_ARCH.startsWith(archPrefix)
    }
}
