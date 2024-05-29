plugins {
    java
    com.github.johnrengelman.shadow
}

tasks {
    shadowJar {
        archiveFileName = "antihealthindicator-${project.version}.jar"
        archiveClassifier = null
    }

    assemble {
        dependsOn(shadowJar)
    }
}