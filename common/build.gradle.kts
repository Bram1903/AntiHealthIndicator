plugins {
    antihealthindicator.`library-conventions`
}

dependencies {
    api(libs.caffeine)
    compileOnlyApi(libs.packetevents.api)
    compileOnlyApi(libs.bundles.adventure)
    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)
}