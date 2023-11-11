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
        val client by creating {
            workingDirectory(file("run"))
            ideaModule = "${rootProject.name}.${name}.main"
            taskName = "AClient"
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                create("modClientRun") {
                    source (sourceSets.main.get())
                    source (project(":Common").sourceSets.main.get())
                }
            }
        }
        afterEvaluate {
            // afterEvaluate so client2 doesn't inherit from this
            client.args += listOf("--username", "APlayer")
        }
        create("client2") {
            parent(client)
            taskName = "BClient"
            args += listOf("--username", "BPlayer")
        }

        create("server") {
            workingDirectory(file("run"))
            ideaModule = "${rootProject.name}.${name}.main"
            taskName = "Server"
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                create("modServerRun") {
                    source (sourceSets.main.get())
                    source (project(":Common").sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(file("run"))
            ideaModule = "${rootProject.name}.${name}.main"
            args += listOf("--mod", property("mod_id") as String, "--all", "--output", "src/generated/resources/", "--existing", "src/main/resources/")
            taskName = "Data"
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                create("modDataRun") {
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

    implementation(group = "none", name = "OC-LuaJ", version = "20220907.1", ext = "jar")
    implementation(group = "none", name = "OC-JNLua", version = "20230530.0", ext = "jar")
    implementation(group = "none", name = "OC-JNLua-Natives", version = "20220928.1", ext = "jar")
}

tasks {
    withType<KotlinCompileTool>().all {
        source(project(":Common").sourceSets.main.get().allSource)
    }

    withType<JavaCompile>().all {
        source(project(":Common").sourceSets.main.get().allJava)
    }

    named<ProcessResources>("processResources") {
        from (project(":Common").sourceSets.main.get().resources)
    }

    jar.configure {
        finalizedBy("reobfJar")
    }

    afterEvaluate {
        jar.configure {
            for (dep in configurations["shadow"]) {
                from(project.zipTree(dep)) {
                    exclude("META-INF/MANIFEST.MF")
                }
            }
        }
    }
}
