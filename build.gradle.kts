group = "com.deathmotion.antihealthindicator"
description = rootProject.name
version = "2.1.0"

tasks {
    register("build") {
        dependsOn(*subprojects.map { it.tasks["build"] }.toTypedArray())
        group = "build"

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

    register<Delete>("clean") {
        dependsOn(*subprojects.map { it.tasks["clean"] }.toTypedArray())
        group = "build"
        delete(rootProject.layout.buildDirectory)
    }
}
