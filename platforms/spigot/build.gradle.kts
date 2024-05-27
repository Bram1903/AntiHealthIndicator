plugins {
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper.packetevents:spigot:2.3.0")
}

tasks {
    // 1.8.8 - 1.16.5 = Java 8
    // 1.17           = Java 16
    // 1.18 - 1.20.4  = Java 17
    // 1-20.5+        = Java 21
    val version = "1.20.6"
    val javaVersion = 21

    val requiredPlugins = runPaper.downloadPluginsSpec {
        url("https://ci.codemc.io/job/retrooper/job/packetevents/lastSuccessfulBuild/artifact/spigot/build/libs/packetevents-spigot-2.3.1-SNAPSHOT.jar")
    }

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    runServer {
        minecraftVersion(version)
        runDirectory.set(file("server/paper/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        downloadPlugins.from(requiredPlugins)
        downloadPlugins {
            url("https://ci.lucko.me/job/spark/410/artifact/spark-bukkit/build/libs/spark-1.10.65-bukkit.jar")
            url("https://ci.lucko.me/job/LuckPerms/lastBuild/artifact/bukkit/loader/build/libs/LuckPerms-Bukkit-5.4.128.jar")
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask() {
        minecraftVersion(version)
        runDirectory.set(file("server/folia/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        downloadPlugins.from(requiredPlugins)

        jvmArgs = jvmArgsExternal
    }
}