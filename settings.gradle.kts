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
            name = "Architectury"
            url = uri("https://maven.architectury.dev/")
        }
    }
    val kotlin_version: String by settings
    resolutionStrategy.eachPlugin {
        if (requested.id.namespace?.startsWith("org.jetbrains.kotlin") == true) useVersion(kotlin_version)
    }
}

rootProject.name = "mcui"
include(
    "common",
    "fabric",
    "forge"
)