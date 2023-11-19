package be.bluexin.mcui.platform

import be.bluexin.mcui.Constants
import be.bluexin.mcui.platform.services.PlatformHelper
import java.util.*

// Service loaders are a built-in Java feature that allow us to locate implementations of an interface that vary from one
// environment to another. In the context of MultiLoader we use this feature to access a mock API in the common code that
// is swapped out for the platform specific implementation at runtime.
object Services {
    // In this example we provide a platform helper which provides information about what platform the mod is running on.
    // For example this can be used to check if the code is running on Forge vs Fabric, or to ask the modloader if another
    // mod is loaded.
    val PLATFORM by lazy { load(PlatformHelper::class.java) }

    // This code is used to load a service for the current environment. Your implementation of the service must be defined
    // manually by including a text file in META-INF/services named with the fully qualified class name of the service.
    // Inside the file you should write the fully qualified class name of the implementation to load for the platform. For
    // example our file on Forge points to ForgePlatformHelper while Fabric points to FabricPlatformHelper.
    fun <T> load(clazz: Class<T>): T {
        val loadedService = ServiceLoader.load(clazz)
            .singleOrNull()
            ?: error("Failed to load service for " + clazz.name)

        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz)
        return loadedService
    }
}