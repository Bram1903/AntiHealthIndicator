plugins {
    antihealthindicator.`java-conventions`
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.paper)
    compileOnly(libs.packetevents.spigot)
    annotationProcessor(libs.lombok)
}