plugins {
    antihealthindicator.`java-conventions`
    `ahi-version`
    `java-library`
}

dependencies {
    api(project(":api"))
    compileOnlyApi(libs.packetevents.api)
    compileOnlyApi(libs.bundles.adventure)
    compileOnlyApi(libs.bundles.adventure.serializers)
    compileOnlyApi(libs.snakeyaml)
    compileOnlyApi(libs.lombok)
    compileOnly(libs.guava)
    annotationProcessor(libs.lombok)

    testImplementation(libs.packetevents.api)
    testImplementation(libs.packetevents.netty.common)
    testImplementation(libs.netty.buffer)
    testImplementation(libs.bundles.adventure)
    testImplementation(libs.adventure.nbt)
    testImplementation(libs.bundles.adventure.serializers)
    testImplementation(libs.guava)
    testImplementation(libs.snakeyaml)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito)
    testRuntimeOnly(libs.junit.platform.launcher)
}

// So that SNAPSHOT is always the latest SNAPSHOT
configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
}

tasks {
    withType<JavaCompile> {
        dependsOn(generateVersionsFile)
    }

    compileTestJava {
        options.release = 21
    }

    test {
        useJUnitPlatform()
    }

    generateVersionsFile {
        packageName = "com.deathmotion.antihealthindicator.util"
    }
}