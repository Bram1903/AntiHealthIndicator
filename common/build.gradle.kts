plugins {
    id("java-library")
}

dependencies {
    compileOnlyApi("com.github.retrooper.packetevents:api:2.3.0")
    compileOnlyApi("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    api("com.github.ben-manes.caffeine:caffeine:2.5.6")

    val adventureVersion = "4.16.0"
    compileOnlyApi("net.kyori:adventure-api:$adventureVersion")
    compileOnlyApi("net.kyori:adventure-text-serializer-gson:$adventureVersion")
}