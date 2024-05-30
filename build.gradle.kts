plugins {
    antihealthindicator.`shadow-conventions`
}

group = "com.deathmotion.antihealthindicator"
description = rootProject.name
version = "2.1.0"

dependencies {
    implementation(project(":common"))
    implementation(project("platforms:bukkit"))
    implementation(project("platforms:velocity"))
}

tasks {
    build {
        dependsOn(*subprojects.map { it.tasks["build"] }.toTypedArray())
        dependsOn(shadowJar)

        doLast {
            val buildOut = project.layout.buildDirectory.dir("libs").get().asFile
            if (!buildOut.exists())
                buildOut.mkdirs()

            for (subproject in subprojects) {
                val subIn = subproject.layout.buildDirectory.dir("libs").get()

                copy {
                    from(subIn)
                    into(buildOut)
                }
            }
        }
    }
}
