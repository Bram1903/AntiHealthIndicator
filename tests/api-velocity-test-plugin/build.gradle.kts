plugins {
    id("java")
    antihealthindicator.`java-conventions`
    alias(libs.plugins.shadow)
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(project(":api"))

    compileOnly(libs.velocity)
    compileOnly(libs.packetevents.velocity)
    annotationProcessor(libs.velocity)
    annotationProcessor(libs.lombok)
}

group = "com.deathmotion.antihealthindicator.testplugin"
version = "1.0.0-SNAPSHOT"

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "AntiHealthIndicatorAPI-Velocity-Test.jar"
        archiveClassifier = null
    }

    assemble {
        dependsOn(shadowJar)
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching(listOf("velocity-plugin.json", "velocity-plugin.json")) {
            expand(
                "version" to project.version,
            )
        }
    }
}