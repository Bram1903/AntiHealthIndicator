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
    version = "2.0.0"

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
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

            relocate("com.google.gson", "com.deathmotion.antihealthindicator.shaded.gson")
            relocate("org.bstats", "com.deathmotion.antihealthindicator")
        }
    }
}