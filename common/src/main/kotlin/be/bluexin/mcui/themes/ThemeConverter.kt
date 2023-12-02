package be.bluexin.mcui.themes

import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.themes.elements.Hud
import be.bluexin.mcui.util.AbstractLuaDecoder
import be.bluexin.mcui.util.AbstractLuaEncoder
import kotlinx.serialization.decodeFromString
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.system.measureTimeMillis

object ThemeConverter {

    private val logger by lazy { LogManager.getLogger(javaClass) }

    @JvmStatic
    fun main(args: Array<String>) {
//        Launch.blackboard = mapOf("fml.deobfuscatedEnvironment" to true)
        if (args.size != 2) {
            logger.error(
                "Wrong arguments provided : {}, expected [xml theme path] [output file path]",
                args.joinToString(prefix = "[", postfix = "]")
            )
            return
        }
        val toRead = File(args[0])
        val toWrite = File(args[1])
        val (hud, fragments) = try {
            logger.info("Loading $toRead")
            clearAndLogErrors {
                val hud = XmlThemeLoader.loadHud(toRead)
                val fragmentRoot = toRead.toPath().resolveSibling("fragments")
                val ns = fragmentRoot.parent.name
                val fragments = buildMap {
                    if (fragmentRoot.isDirectory()) {
                        fragmentRoot.listDirectoryEntries().forEach {
                            put(
                                ResourceLocation(
                                    ns,
                                    it.nameWithoutExtension
                                )
                            ) { XmlThemeLoader.loadFragment(it.toFile()) }
                        }
                    }
                }
                hud.setup(fragments)

                hud to fragments
            }
        } catch (e: Exception) {
            val message = "Something went wrong reading $toRead"
            logger.error(message, e)
            return
        }
        try {
            logger.info("Exporting to $toWrite")
            clearAndLogErrors {
                toWrite.parentFile.mkdirs()
                JsonThemeLoader.export(hud, toWrite)
                if (fragments.isNotEmpty()) {
                    val fragmentExportRoot = toWrite.toPath().resolveSibling("fragments")
                    fragmentExportRoot.toFile().mkdirs()
                    fragments.forEach { (key, fragment) ->
                        JsonThemeLoader.export(fragment(), fragmentExportRoot.resolve("${key.path}.json").toFile())
                    }
                }
            }
        } catch (e: Exception) {
            val message = "Something went wrong writing $toWrite"
            logger.error(message, e)
            return
        }
        logger.info("Converted $toRead to $toWrite. Checking loading")

        val time = measureTimeMillis {
            clearAndLogErrors {
                val read = JsonThemeLoader.loadHud(toWrite)
                val fragmentRoot = toWrite.toPath().resolveSibling("fragments")
                val ns = fragments.keys.first().namespace
                val readFragments = buildMap {
                    if (fragmentRoot.isDirectory()) {
                        fragmentRoot.listDirectoryEntries().forEach {
                            put(
                                ResourceLocation(
                                    ns,
                                    it.nameWithoutExtension
                                )
                            ) { JsonThemeLoader.loadFragment(it.toFile()) }
                        }
                    }
                }

                read.setup(readFragments)
            }
        }
        logger.info("Read in $time ms.")
    }

    private inline fun <T> clearAndLogErrors(body: () -> T): T {
        AbstractThemeLoader.Reporter.errors.clear()

        val r = body()

        if (AbstractThemeLoader.Reporter.errors.isNotEmpty()) {
            logger.warn("${AbstractThemeLoader.Reporter.errors.size} errors detected :")
            AbstractThemeLoader.Reporter.errors.forEach(logger::warn)
        }

        return r
    }
}

object XmlTests {
    @JvmStatic
    fun main(args: Array<String>) {
        val xml = XmlThemeLoader.xml

        val iss = javaClass.classLoader.getResourceAsStream("assets/saoui/themes/hex2/fragments/label.xml")
            ?: error("Couldn't load iss")
        val frag = xml.decodeFromString<Fragment>(
            iss.reader().readText()
        )
        println(frag)

        val lua = AbstractLuaEncoder.LuaEncoder().apply {
            encodeSerializableValue(Fragment.serializer(), frag)
        }.data

        val luaFrag = AbstractLuaDecoder.LuaDecoder(lua).run {
            decodeSerializableValue(Fragment.serializer())
        }

        val iss2 = javaClass.classLoader.getResourceAsStream("assets/saoui/themes/hex2/hud.xml")
            ?: error("Couldn't load iss")

        val hud = xml.decodeFromString<Hud>(
            iss2.reader().readText()
        )
        println(hud)
    }
}
