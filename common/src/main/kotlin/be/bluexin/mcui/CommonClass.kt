package be.bluexin.mcui

import be.bluexin.mcui.config.ConfigHandler
import be.bluexin.mcui.config.OptionCore
import be.bluexin.mcui.config.Settings
import be.bluexin.mcui.platform.Services

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
object CommonClass {
    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    fun init() {
        Constants.LOG.info(
            "Hello from Kotlin Common init on {}! we are currently in a {} environment!",
            Services.PLATFORM.platformName,
            Services.PLATFORM.environmentName
        )
        OptionCore.Initializer.registerSettings()
        ConfigHandler.registerSettings()
        Settings.build(Settings.NS_BUILTIN)
    }
}