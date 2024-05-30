plugins {
    java
    com.github.johnrengelman.shadow
}

tasks {
    shadowJar {
        archiveFileName = "AntiHealthIndicator-${project.version}.jar"
        archiveClassifier = null

        relocate("com.github.benmanes.caffeine", "com.deathmotion.antihealthindicator.shaded.caffeine")
        relocate(
            "net.kyori.adventure.text.serializer.gson",
            "io.github.retrooper.packetevents.adventure.serializer.gson"
        )
        relocate(
            "net.kyori.adventure.text.serializer.legacy",
            "io.github.retrooper.packetevents.adventure.serializer.legacy"
        )
    }

    assemble {
        dependsOn(shadowJar)
    }
}