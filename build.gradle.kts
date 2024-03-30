plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("java-library")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "xyz.jpenilla.run-paper")
    apply(plugin = "java-library")

    group = "com.deathmotion.antihealthindicator"
    version = "1.1.3"

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-releases/")
    }

    tasks {
        build {
            dependsOn("shadowJar")
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
        shadowJar {
            minimize()
            archiveFileName.set("${project.name}-${project.version}.jar")

            relocate("io.github.retrooper.packetevents", "com.deathmotion.antihealthindicator.shaded.io.github.retrooper.packetevents")
            relocate("com.github.retrooper.packetevents", "com.deathmotion.antihealthindicator.shaded.com.github.retrooper.packetevents")
            relocate("net.kyori", "com.deathmotion.antihealthindicator.shaded.kyori")
            relocate("com.google.gson", "com.deathmotion.antihealthindicator.shaded.gson")
            relocate("org.bstats", "com.deathmotion.antihealthindicator")
        }
    }
}