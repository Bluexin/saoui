@file:Suppress("LocalVariableName")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Instant

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.3-SNAPSHOT" apply false
}

architectury {
    minecraft = rootProject.property("minecraft_version").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    if (hasProperty("serialization_version")) apply<SerializationGradleSubplugin>()


    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    dependencies {
        "minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")
        "mappings"(
            loom.layered {
                officialMojangMappings()
                parchment("org.parchmentmc.data:parchment-${property("minecraft_version")}:${property("mappings_version")}")
            }
        )
    }


}

allprojects {
    apply<JavaPlugin>()
    apply<MavenPublishPlugin>()
    apply<KotlinPlatformJvmPlugin>()
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    base.archivesName.set(rootProject.property("mod_id").toString())
    //base.archivesBaseName = rootProject.property("archives_base_name").toString()
    version = rootProject.property("version").toString()
    group = rootProject.property("group").toString()

    repositories {
        mavenCentral()

        maven {
            name = "Sponge / Mixin"
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
        }

        maven {
            name = "BlameJared Maven (CrT / Bookshelf)"
            url = uri("https://maven.blamejared.com")
        }
        maven {
            name = "Bluexin"
            url = uri("https://maven.bluexin.be/repository/releases/")
        }
        maven {
            name = "Sonatype OSSRH (Snapshots)"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        ivy {
            // Pulled from https://github.com/MightyPirates/OpenComputers/blob/1c0dc67182292895495cb0d421ec0f529d243d74/build.gradle
            name = "asie dependency mirror"
            artifactPattern("https://asie.pl/javadeps/[module]-[revision](-[classifier]).[ext]")
            metadataSources.artifact()
        }
        maven {
            url = uri("https://maven.saps.dev/releases")
            content {
                includeGroup ("dev.latvian.mods")
                includeGroup ("dev.ftb.mods")
            }
        }
        maven {
            url = uri("https://maven.saps.dev/snapshots")
            content {
                includeGroup ("dev.latvian.mods")
                includeGroup ("dev.ftb.mods")
            }
        }
        maven {
            name = "Modrinth"
            url  = uri("https://api.modrinth.com/maven")
            content {
                includeGroup ("maven.modrinth")
            }
        }

    }


    dependencies {
        "compileClasspath"(rootProject.project.libs.kotlinGradlePlugin)
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${property("coroutines_version")}"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
        if (hasProperty("serialization_version")) {
            implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:${property("serialization_version")}"))
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm")
        }

        implementation("org.joml:joml:${property("jomlVersion")}")
        implementation("be.bluexin.gnu.jel:gnu-jel:2.1.3")
        implementation("com.helger:ph-css:6.5.0")
        implementation("com.helger.commons:ph-commons:10.1.6")
        implementation("org.slf4j:slf4j-api:1.7.36")
        implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
        implementation("com.sun.xml.bind:jaxb-impl:3.0.2")

        implementation("net.sandius.rembulan:rembulan-compiler:0.1-SNAPSHOT")
        implementation("net.sandius.rembulan:rembulan-stdlib:0.1-SNAPSHOT")

        implementation("org.classdump.luna:luna-all-shaded:0.4.1")

        implementation(group = "none", name = "OC-LuaJ", version = "20220907.1", ext = "jar")
        implementation(group = "none", name = "OC-JNLua", version = "20220928.1", ext = "jar")
        implementation(group = "none", name = "OC-JNLua-Natives", version = "20220928.1", ext = "jar")

    }


    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(8)
    }

    java {
        withSourcesJar()
    }

    val compileKotlin: KotlinCompile by tasks
    compileKotlin.kotlinOptions {
        jvmTarget = "1.8"
    }
    val compileTestKotlin: KotlinCompile by tasks
    compileTestKotlin.kotlinOptions {
        jvmTarget = "1.8"
    }
}
