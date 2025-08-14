plugins {
    java
}

group = rootProject.group
version = rootProject.version
description = project.description

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    disableAutoTargetJvm()
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 8
    }

    withType<Test> {
        failOnNoDiscoveredTests = false
    }

    processResources {
        inputs.properties(
            "version" to rootProject.ext["versionNoHash"].toString()
        )

        filesMatching(listOf("plugin.yml", "bungee.yml", "velocity-plugin.json")) {
            expand(
                "version" to rootProject.ext["versionNoHash"].toString()
            )
        }
    }

    defaultTasks("build")
}