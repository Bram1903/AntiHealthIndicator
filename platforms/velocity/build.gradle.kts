plugins {
    antihealthindicator.`java-conventions`
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.packetevents.velocity)
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

tasks.register("generateTemplates") {}
