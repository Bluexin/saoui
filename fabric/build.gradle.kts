
plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentFabric: Configuration = configurations.getByName("developmentFabric")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentFabric.extendsFrom(configurations["common"])
}

// DO NOT REMOVE
configurations.all {
    resolutionStrategy {
        force(libs.fabric.loader)
    }
}

dependencies {
    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)
    // Remove the next line if you don't want to depend on the API
    modApi(libs.architectury.fabric)

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", configuration = "transformProductionFabric")) { isTransitive = false }
    modImplementation(libs.fabric.languageKotlin)
    modApi ("dev.ftb.mods:ftb-library-fabric:${property("ftb_library_version")}") { isTransitive = false }
    modApi ("dev.ftb.mods:ftb-teams-fabric:${property("ftb_teams_version")}") { isTransitive = false }

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

    modApi ("maven.modrinth:forge-config-api-port:p2C10JUL")
    implementation   ("com.electronwill.night-config:core:3.6.3")
    implementation   ("com.electronwill.night-config:toml:3.6.3")
}

val javaComponent = components.getByName<AdhocComponentWithVariants>("java")
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("fabric")
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }



    publishing {
        publications {
            create<MavenPublication>("mavenFabric") {
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