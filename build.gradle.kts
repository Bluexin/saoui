@file:Suppress("LocalVariableName")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin
import java.time.Instant

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
    id("com.matyrobbrt.mc.registrationutils")
}

registrationUtils {
    group("$group.registration")
    projects {
        register("Common") {
            type("common")
        }
        register("Fabric") {
            type("fabric")
        }
        register("Forge") {
            type("forge")
        }
    }
}

subprojects {
    apply<JavaPlugin>()
    apply<MavenPublishPlugin>()
    apply<KotlinPlatformJvmPlugin>()
    if (hasProperty("serialization_version")) apply<SerializationGradleSubplugin>()

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        withSourcesJar()
    }

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
        /*maven {
            name = "Tencao Maven"
            url = uri("https://maven.tencao.com/repository/releases/")
        }*/
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
    }

    val shadow by configurations.creating {
        isTransitive = false
        configurations.named("implementation") {
            extendsFrom(this@creating)
        }
    }

    val shadowRuntime by configurations.creating {
        isTransitive = false
        configurations.named("runtimeOnly") {
            extendsFrom(this@creating)
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${property("coroutines_version")}"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
        if (hasProperty("serialization_version")) {
            implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:${property("serialization_version")}"))
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm")
        }

        shadow("be.bluexin.gnu.jel:gnu-jel:2.1.3")
        shadow("com.helger:ph-css:6.5.0")
        shadow("com.helger.commons:ph-commons:10.1.6")
        shadow("org.slf4j:slf4j-api:1.7.36")
        shadow("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
        shadowRuntime("com.sun.xml.bind:jaxb-impl:3.0.2")

        shadow("net.sandius.rembulan:rembulan-compiler:0.1-SNAPSHOT")
        shadow("net.sandius.rembulan:rembulan-stdlib:0.1-SNAPSHOT")

        shadow("org.classdump.luna:luna-all-shaded:0.4.1")

        shadow(group = "none", name = "OC-LuaJ", version = "20220907.1", ext = "jar")
        shadow(group = "none", name = "OC-JNLua", version = "20230530.0", ext = "jar")
        shadow(group = "none", name = "OC-JNLua-Natives", version = "20220928.1", ext = "jar")
    }

    tasks {
        val mod_name: String by project

        jar.configure {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            from(rootProject.file("LICENSE")) {
                rename { "${it}_${mod_name}" }
            }

            manifest {
                val mod_author: String by project
                val minecraft_version: String by project

                attributes(
                    "Specification-Title" to mod_name,
                    "Specification-Vendor" to mod_author,
                    "Specification-Version" to archiveVersion,
                    "Implementation-Title" to name,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to mod_author,
                    "Implementation-Timestamp" to Instant.now().toString(),
                    "Timestamp" to System.currentTimeMillis(),
                    "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                    "Built-On-Minecraft" to minecraft_version
                )
            }
        }

        named<Jar>("sourcesJar") {
            from(rootProject.file("LICENSE")) {
                rename { "${it}_${mod_name}" }
            }
        }
        withType<KotlinCompilationTask<KotlinJvmCompilerOptions>>().all {
            compilerOptions {
                javaParameters.set(true)
                jvmTarget.set(JvmTarget.JVM_17)
                apiVersion.set(KotlinVersion.KOTLIN_1_8)
                languageVersion.set(KotlinVersion.KOTLIN_1_8)
            }
        }
        withType<GenerateModuleMetadata>().all {
            enabled = false
        }
        withType<JavaCompile>().all {
            options.encoding = "UTF-8"
            options.release.set(17)
        }
    }

    publishing {
        publications {
            register<MavenPublication>("mavenJava") {
                groupId = group.toString()
                artifactId = archivesName.get()
                version = version as String
                artifact(tasks.named("jar"))
                from(components["java"])
            }
        }

        repositories {
            maven {
                url = if (hasProperty("local_maven"))  uri("file://${property("local_maven")}")
                    else uri("file://$buildDir/.m2")
            }
        }
    }
}
