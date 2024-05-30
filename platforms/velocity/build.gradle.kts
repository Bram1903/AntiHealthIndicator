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
    compileOnly(libs.velocity)
    compileOnly(libs.packetevents.velocity)
    annotationProcessor(libs.velocity)
}

tasks.register("generateTemplates") {}
