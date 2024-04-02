dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("dev.folia:folia-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper.packetevents:spigot:2.2.1")
    implementation(project(":common"))
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks {

    runServer {
        // The version of the server to run
        val version = "1.20.4"

        minecraftVersion(version)
        runDirectory.set(file("server/$version"))

        // 1.8.8 - 1.16.5 = Java 8
        // 1.17           = Java 16
        // 1.18 - 1.20.4  = Java 17
        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        })

        downloadPlugins {
            url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
            url("https://ci.lucko.me/job/spark/400/artifact/spark-bukkit/build/libs/spark-1.10.59-bukkit.jar")
            url("https://download.luckperms.net/1530/bukkit/loader/LuckPerms-Bukkit-5.4.117.jar")
        }

        jvmArgs = listOf(
                "-Dcom.mojang.eula.agree=true"
        )
    }
}