plugins {
    antihealthindicator.`java-conventions`
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.spigot)
    compileOnly(libs.packetevents.spigot)
}