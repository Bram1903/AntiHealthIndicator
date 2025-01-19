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

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    disableAutoTargetJvm()
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 8
    }

    processResources {
        inputs.property("version", rootProject.ext["versionNoHash"])
        filesMatching(listOf("plugin.yml", "bungee.yml", "velocity-plugin.json")) {
            expand("version" to rootProject.ext["versionNoHash"])
        }
    }

    defaultTasks("build")
}