
plugins {
    idea
}

architectury {
    val enabled_platforms: String by rootProject
    common(enabled_platforms.split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/${property("mod_id")}.accesswidener"))
}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation(libs.fabric.loader)
    // Remove the next line if you don't want to depend on the API
    modApi(libs.architectury)
    compileOnly(kotlin("stdlib-jdk8"))
    implementation("com.google.code.findbugs:jsr305:3.0.1")
    modApi ("dev.ftb.mods:ftb-library-fabric:${property("ftb_library_version")}")
    modApi ("dev.ftb.mods:ftb-teams-fabric:${property("ftb_teams_version")}")
    modApi ("maven.modrinth:forge-config-api-port:p2C10JUL")
    implementation   ("com.electronwill.night-config:core:3.6.3")
    implementation   ("com.electronwill.night-config:toml:3.6.3")
    //modImplementation ("curse.maven:forge-config-api-port-fabric-547434:3943246")
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("pack.mcmeta") {
        expand(properties.toMap())
    }
}
