plugins {
    antihealthindicator.`library-conventions`
    alias(libs.plugins.run.velocity)
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.packetevents.velocity)
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

tasks.register("generateTemplates") {}

tasks {
    runVelocity {
        velocityVersion("3.3.0-SNAPSHOT")
        runDirectory.set(file("server/velocity/"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        })

        downloadPlugins {
            url("https://ci.codemc.io/job/retrooper/job/packetevents/lastSuccessfulBuild/artifact/velocity/build/libs/packetevents-velocity-2.3.1-SNAPSHOT.jar")
        }
    }
}
