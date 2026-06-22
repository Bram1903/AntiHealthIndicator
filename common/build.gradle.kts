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
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.bundles.adventure)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly(libs.packetevents.api)
}

// So that SNAPSHOT is always the latest SNAPSHOT
configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
}

tasks {
    withType<JavaCompile> {
        dependsOn(generateVersionsFile)
    }

    test {
        useJUnitPlatform()
    }

    generateVersionsFile {
        packageName = "com.deathmotion.antihealthindicator.util"
    }
}
