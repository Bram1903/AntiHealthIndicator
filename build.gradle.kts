plugins {
    java
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "xyz.jpenilla.run-paper")

    group = "com.deathmotion.antihealthindicator"
    version = "2.0.0-SNAPSHOT"

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-releases/")
    }
}

dependencies {
    api("com.github.ben-manes.caffeine:caffeine:2.5.6")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        project.subprojects.forEach { subproject ->
            from(project(subproject.path).sourceSets.main.get().output)
        }

        relocate("com.github.benmanes.caffeine", "com.deathmotion.antihealthindicator.shaded.caffeine")
        minimize()
    }
}
