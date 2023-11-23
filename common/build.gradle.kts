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
//    modApi(libs.architectury)
    compileOnly(kotlin("stdlib-jdk8"))
    implementation(libs.jsr305)
//    modApi(libs.bundles.ftb.fabric)
    modApi(libs.forge.config)
    implementation(libs.bundles.nightconfig)
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("pack.mcmeta") {
        expand(properties.toMap())
    }
}
