@file:Suppress("PropertyName", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool

plugins {
    id("fabric-loom") version "1.0-SNAPSHOT"
    idea
}

val minecraft_version: String by project
archivesName.set("${property("mod_name")}-fabric-$minecraft_version")

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    implementation("com.google.code.findbugs:jsr305:3.0.1")
    implementation(project(":Common"))
}

loom {
    runs {
        val client by named("client") {
            client()
            configName = "Fabric AClient"
            isIdeConfigGenerated = true
            runDir = "run"
        }
        afterEvaluate {
            client.programArgs += listOf("--username", "APlayer")
        }
        register("client2") {
            inherit(client)
            configName = "Fabric BClient"
            programArgs += listOf("--username", "BPlayer")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            isIdeConfigGenerated = true
            runDir = "run"
        }
    }

    accessWidenerPath.set(project(":Common").file("src/main/resources/${property("mod_id")}.accesswidener"))
}

tasks {
    named<ProcessResources>("processResources") {
        from(project(":Common").sourceSets.main.get().resources)
        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to version))
        }
    }

    withType<KotlinCompileTool>().all {
        source(project(":Common").sourceSets.main.get().allSource)
    }

    withType<JavaCompile>().all {
        source(project(":Common").sourceSets.main.get().allJava)
    }
}
