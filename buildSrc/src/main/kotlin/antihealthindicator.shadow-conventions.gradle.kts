plugins {
    java
    com.github.johnrengelman.shadow
}

tasks {
    shadowJar {
        archiveFileName = "AntiHealthIndicator-${project.version}.jar"
        archiveClassifier = null
    }

    assemble {
        dependsOn(shadowJar)
    }
}