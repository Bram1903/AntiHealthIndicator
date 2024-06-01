plugins {
    antihealthindicator.`java-conventions`
    alias(libs.plugins.paperweight.userdev)
}

dependencies {
    implementation(project(":common"))
    paperweight.paperDevBundle(libs.versions.paper)
    compileOnly(libs.packetevents.spigot)
}