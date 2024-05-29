plugins {
    antihealthindicator.`library-conventions`
    antihealthindicator.`shadow-conventions`
}

dependencies {
    api(libs.caffeine)
    compileOnlyApi(libs.packetevents.api)
    compileOnlyApi(libs.bundles.adventure)
    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)
}