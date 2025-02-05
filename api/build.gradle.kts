plugins {
    antihealthindicator.`java-conventions`
    `java-library`
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    jar {
        archiveFileName = "${rootProject.name}API-${rootProject.ext["versionNoHash"]}.jar"
        archiveClassifier = null
    }

    named<Jar>("javadocJar") {
        archiveFileName.set("${rootProject.name}API-${rootProject.ext["versionNoHash"]}-javadoc.jar")
    }

    named<Jar>("sourcesJar") {
        archiveFileName.set("${rootProject.name}API-${rootProject.ext["versionNoHash"]}-sources.jar")
    }

    javadoc {
        title = "AntiHealthIndicator API v${rootProject.ext["versionNoHash"]}"
        options.encoding = Charsets.UTF_8.name()
        options {
            (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        }
    }
}