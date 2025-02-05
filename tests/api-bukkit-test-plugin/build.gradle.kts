plugins {
    id("java")
    antihealthindicator.`java-conventions`
    alias(libs.plugins.shadow)
}

repositories {
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    compileOnly(project(":api"))

    compileOnly(libs.paper)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

group = "com.deathmotion.antihealthindicator.testplugin"
version = "1.0.0-SNAPSHOT"

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "APITestPlugin.jar"
        archiveClassifier = null
    }

    assemble {
        dependsOn(shadowJar)
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand(
                "version" to project.version,
            )
        }
    }
}