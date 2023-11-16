@file:Suppress("PropertyName", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool

plugins {
    id("net.minecraftforge.gradle") version "5.1.+"
    idea
}

val minecraft_version: String by project
archivesName.set("${property("mod_name")}-forge-$minecraft_version")

jarJar.enable()

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
                create("mcui") {
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

/*val shadowRuntime2 by configurations.creating {
    isTransitive = false
    configurations.minecraftLibrary {
        extendsFrom(this@creating)
    }
}*/

configurations {
    minecraftLibrary {
        extendsFrom(minecraftEmbed.get())
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${property("forge_version")}")
    compileOnly(project(":Common"))
    implementation("thedarkcolour:kotlinforforge:${property("forge_kotlin_version")}")

//    implementation(group = "none", name = "OC-LuaJ", version = "20220907.1", ext = "jar")
//    implementation(group = "none", name = "OC-JNLua", version = "20230530.0", ext = "jar")
//    jarJar(group = "none", name = "OC-JNLua-Natives", version = "20220928.1", ext = "jar")
    /*minecraftEmbed(group = "none", name = "OC-JNLua-Natives", version = "20220928.1"*//*, ext = "jar"*//*) {
        jarJar.ranged(this, "[20220928.1,20240101.0)")
    }*/
    minecraftEmbed("com.github.wagyourtail.luaj:luaj-jse:05e2b7d76a") {
        jarJar.ranged(this, "05e2b7d76a")
    }

    // TODO : take out what we don't actually need
    minecraftEmbed("be.bluexin.gnu.jel:gnu-jel:2.1.3") {
        jarJar.ranged(this, "[2.1,3)")
    }
    minecraftEmbed("com.helger:ph-css:6.5.0") {
        exclude(group = "com.google.code.findbugs")
        jarJar.ranged(this, "[6.5,7)")
    }
    minecraftEmbed("com.helger.commons:ph-commons:10.1.6") {
        exclude(group = "com.google.code.findbugs")
        jarJar.ranged(this, "[10.1,11)")
    }
    minecraftEmbed("org.slf4j:slf4j-api:1.7.36") { // is this one really necessary ?
        jarJar.ranged(this, "[1.7,2)")
    }
    minecraftEmbed("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1") {
        jarJar.ranged(this, "[3.0,4)")
    }
    minecraftEmbed("com.sun.xml.bind:jaxb-impl:3.0.2") {
        jarJar.ranged(this, "[3.0,4)")
    }
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
            /*for (dep in configurations.shadow.get()) {
                from(project.zipTree(dep)) {
                    exclude("META-INF/MANIFEST.MF")
                }
            }*/
//            for (dep in shadowRuntime2) {
//                from(project.zipTree(dep)) {
//                    exclude("META-INF", "META-INF/**")
//                }
//            }
        }
    }

    this.jarJar {
        finalizedBy("reobfJarJar")
        archiveClassifier.set("")
    }

    assemble { dependsOn("jarJar") }
}
