@file:Suppress("LocalVariableName")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "Forge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Sponge Snapshots"
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
        }
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org/")
        }
    }
    val kotlin_version: String by settings
    val registration_utils_version: String by settings
    resolutionStrategy.eachPlugin {
        if (requested.id.namespace?.startsWith("org.jetbrains.kotlin") == true) useVersion(kotlin_version)
        else when (requested.id.id) {
            "com.matyrobbrt.mc.registrationutils" -> useVersion(registration_utils_version)
        }
    }
}

rootProject.name = "SAOUI"
include(
    "Common",
    "Fabric",
    "Forge"
)