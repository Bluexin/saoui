
plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    forge()
}

val modId: String = rootProject.property("mod_id").toString()
loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
        dataGen {
            mod(modId)
        }
    }

}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }
}

/**
 * @see: https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html
 * */
val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentForge: Configuration = configurations.getByName("developmentForge")
configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentForge.extendsFrom(configurations["common"])
}

val minecraft_version: String by project
val mappings_version: String by project


repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    forge(libs.forge)
    // Remove the next line if you don't want to depend on the API
    modApi(libs.architectury.forge)
    common(project(":commont", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":commont", configuration = "transformProductionForge")) { isTransitive = false }
    implementation(libs.kotlinforforge)
    modApi("dev.ftb.mods:ftb-library-forge:${property("ftb_library_version")}") { isTransitive = false }
    modApi("dev.ftb.mods:ftb-teams-fabric:${property("ftb_teams_version")}") { isTransitive = false }
    shadow("org.joml:joml:${property("jomlVersion")}")
    shadow("be.bluexin.gnu.jel:gnu-jel:2.1.3")
    shadow("com.helger:ph-css:6.5.0")
    shadow("com.helger.commons:ph-commons:10.1.6")
    shadow("org.slf4j:slf4j-api:1.7.36")
    shadow("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    modApi("com.sun.xml.bind:jaxb-impl:3.0.2")

    shadow("net.sandius.rembulan:rembulan-compiler:0.1-SNAPSHOT")
    shadow("net.sandius.rembulan:rembulan-stdlib:0.1-SNAPSHOT")

    shadow("org.classdump.luna:luna-all-shaded:0.4.1")

    shadow(group = "none", name = "OC-LuaJ", version = "20220907.1", ext = "jar")
    shadow(group = "none", name = "OC-JNLua", version = "20220928.1", ext = "jar")
    shadow(group = "none", name = "OC-JNLua-Natives", version = "20220928.1", ext = "jar")
}

val javaComponent = components.getByName<AdhocComponentWithVariants>("java")
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

tasks.runClient {
    classpath += project.files(File(projectDir, "build/classes/kotlin/main"))
}

tasks.runServer {
    classpath += project.files(File(projectDir, "build/classes/kotlin/main"))
}

tasks {
    processResources {
        inputs.property("version", project.version)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE


        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("forge")
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }



    publishing {
        publications {
            create<MavenPublication>("mavenForge") {
                artifactId = "${rootProject.property("group")}-${project.name}"
                from(javaComponent)
            }
        }

        // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
        repositories {
            // Add repositories to publish to here.
        }
    }
}