plugins {
    antihealthindicator.`java-conventions`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.run.velocity)
}

group = "com.deathmotion.antihealthindicator"
description = "A plugin that prevents hackers and modders from seeing the health of other players."
val fullVersion = "2.5.0"
val snapshot = false

fun getVersionMeta(includeHash: Boolean): String {
    if (!snapshot) {
        return ""
    }

    var commitHash = ""
    if (includeHash && file(".git").isDirectory) {
        val result = providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get().trim()

        commitHash = "+$result"
    }

    return "$commitHash-SNAPSHOT"
}
version = "$fullVersion${getVersionMeta(true)}"
ext["versionNoHash"] = "$fullVersion${getVersionMeta(false)}"

dependencies {
    implementation(project(":common"))
    implementation(project(":platforms:bukkit"))
    implementation(project(":platforms:velocity"))
    implementation(project(":platforms:bungeecord"))
    implementation(project(":platforms:sponge"))
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${ext["versionNoHash"]}.jar"
        archiveClassifier = null

        relocate(
            "net.kyori.adventure.text.serializer.gson",
            "io.github.retrooper.packetevents.adventure.serializer.gson"
        )
        relocate(
            "net.kyori.adventure.text.serializer.legacy",
            "io.github.retrooper.packetevents.adventure.serializer.legacy"
        )

        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    // 1.8.8 - 1.16.5 = Java 8
    // 1.17           = Java 16
    // 1.18 - 1.20.4  = Java 17
    // 1-20.5+        = Java 21
    val version = "1.21.11"
    val javaVersion = JavaLanguageVersion.of(21)

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    val sharedBukkitPlugins = runPaper.downloadPluginsSpec {
        url("https://cdn.modrinth.com/data/HYKaKraK/versions/yu8xYp9k/packetevents-spigot-2.11.0.jar")
        url("https://github.com/ViaVersion/ViaVersion/releases/download/5.5.1/ViaVersion-5.5.1.jar")
        url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.5.1/ViaBackwards-5.5.1.jar")
    }

    runServer {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/paper/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        //serverJar(file("./1.8.8.jar"))

        downloadPlugins {
            from(sharedBukkitPlugins)
            url("https://github.com/EssentialsX/Essentials/releases/download/2.21.2/EssentialsX-2.21.2.jar")
            url("https://download.luckperms.net/1602/bukkit/loader/LuckPerms-Bukkit-5.5.15.jar")
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/folia/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            from(sharedBukkitPlugins)
        }

        jvmArgs = jvmArgsExternal
    }

    runVelocity {
        velocityVersion("3.3.0-SNAPSHOT")
        runDirectory = rootDir.resolve("run/velocity/")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            url("https://ci.codemc.io/job/retrooper/job/packetevents/lastSuccessfulBuild/artifact/velocity/build/libs/packetevents-velocity-2.6.1-SNAPSHOT.jar")
            url("https://ci.lucko.me/job/spark/418/artifact/spark-velocity/build/libs/spark-1.10.73-velocity.jar")
        }
    }
}
