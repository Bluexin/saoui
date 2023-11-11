@file:Suppress("PropertyName", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

val minecraft_version: String by project
archivesName.set("${property("mod_name")}-common-$minecraft_version")

minecraft {
    version(minecraft_version)
    runs {
        if ((findProperty("common_runs_enabled") as? String).toBoolean()) {
            server(findProperty("common_server_run_name") as? String ?: "vanilla_server") {
                workingDirectory(file("run"))
            }
            client(findProperty("common_client_run_name") as? String ?: "vanilla_client") {
                workingDirectory(file("run"))
            }
        }
    }
    accessWideners(file("src/main/resources/${property("mod_id")}.accesswidener"))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    implementation("com.google.code.findbugs:jsr305:3.0.1")
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("pack.mcmeta") {
        expand(properties.toMap())
    }
}
