plugins {
    antihealthindicator.`java-conventions`
    `ahi-version`
    `java-library`
}

dependencies {
    api(project(":api"))
    compileOnlyApi(libs.packetevents.api)
    compileOnlyApi(libs.bundles.adventure)
    compileOnlyApi(libs.snakeyaml)
    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)
}

// So that SNAPSHOT is always the latest SNAPSHOT
configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
}

tasks {
    withType<JavaCompile> {
        dependsOn(generateVersionsFile)
    }

    generateVersionsFile {
        packageName = "com.deathmotion.antihealthindicator.util"
    }
}