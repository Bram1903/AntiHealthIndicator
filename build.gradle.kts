plugins {
    antihealthindicator.`shadow-conventions`
    antihealthindicator.`library-conventions`
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
    val excludedModule = "platforms"

    build {
        dependsOn(*subprojects.filter { it.name != excludedModule }.map { it.tasks["build"] }.toTypedArray())
        dependsOn(shadowJar)

        doLast {
            val buildOut = project.layout.buildDirectory.dir("libs").get().asFile
            if (!buildOut.exists())
                buildOut.mkdirs()

            for (subproject in subprojects.filter { it.name != excludedModule }) {
                val subIn = subproject.layout.buildDirectory.dir("libs").get()

                copy {
                    from(subIn)
                    into(buildOut)
                }
            }
        }
    }

    clean<Delete> {
        dependsOn(*subprojects.filter { it.name != excludedModule }.map { it.tasks["clean"] }.toTypedArray())
        group = "build"
        delete(rootProject.layout.buildDirectory)
    }

    defaultTasks("build")
}
