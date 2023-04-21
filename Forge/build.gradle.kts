@file:Suppress("PropertyName", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool

plugins {
    id("net.minecraftforge.gradle") version "5.1.+"
}

val minecraft_version: String by project
archivesName.set("${property("mod_name")}-forge-$minecraft_version")

minecraft {
    mappings(/*channel = */"official", /*version = */minecraft_version)

    if ((findProperty("forge_ats_enabled") as? String).toBoolean()) {
        accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
        logger.debug("Forge Access Transformers are enabled for this project")
    }

    runs {
        val client by registering {
            workingDirectory(file("run"))
            ideaModule = "${rootProject.name}.${name}.main"
            taskName = "AClient"
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                register("modClientRun") {
                    source (sourceSets.main.get())
                    source (project(":Common").sourceSets.main.get())
                }
            }
        }
        afterEvaluate {
            // afterEvaluate so client2 doesn"t inherit from this
            client.get().args += listOf("--username", "APlayer")
        }
        register("client2") {
            parent(client.get())
            taskName = "BClient"
            args += listOf("--username", "BPlayer")
        }

        register("server") {
            workingDirectory(file("run"))
            ideaModule = "${rootProject.name}.${name}.main"
            taskName = "Server"
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                register("modServerRun") {
                    source (sourceSets.main.get())
                    source (project(":Common").sourceSets.main.get())
                }
            }
        }

        register("data") {
            workingDirectory(file("run"))
            ideaModule = "${rootProject.name}.${name}.main"
            args += listOf("--mod", property("mod_id") as String, "--all", "--output", "src/generated/resources/", "--existing", "src/main/resources/")
            taskName = "Data"
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                register("modDataRun") {
                    source (sourceSets.main.get())
                    source (project(":Common").sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main.configure {
    resources.srcDir("src/generated/resources")
}

repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${property("forge_version")}")
    compileOnly(project(":Common"))
    implementation("thedarkcolour:kotlinforforge:${property("forge_kotlin_version")}")
}

tasks {
    withType<KotlinCompileTool>().configureEach {
        source(project(":Common").sourceSets.main.get().allSource)
    }

    named<ProcessResources>("processResources") {
        from (project(":Common").sourceSets.main.get().resources)
    }

    named("jar") {
        finalizedBy("reobfJar")
    }
}
