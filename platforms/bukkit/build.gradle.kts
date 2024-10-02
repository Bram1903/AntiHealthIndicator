plugins {
    antihealthindicator.`java-conventions`
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.paper)
    compileOnly(libs.packetevents.spigot)
    compileOnly(libs.betterreload.api)
}