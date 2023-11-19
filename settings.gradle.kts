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
        maven {
            name = "Architectury"
            url = uri ("https://maven.architectury.dev/")
        }
    }
}

rootProject.name = "saoui"
include(
    "common",
    "fabric",
    "forge"
)