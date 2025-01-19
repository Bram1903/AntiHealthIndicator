import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    antihealthindicator.`java-conventions`
    alias(libs.plugins.spongeGradle)
}

repositories {
    maven {
        name = "sponge"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.sponge)
    compileOnly(libs.packetevents.sponge)
    annotationProcessor(libs.lombok)
}

sponge {
    apiVersion("8.1.0")
    license("GPL3")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("antihealthindicator") {
        displayName("AntiHealthIndicator")
        entrypoint("com.deathmotion.antihealthindicator.AHISponge")
        description("Prevent health indicators from being displayed on the client")
        version(project.version.toString())
        contributor("Bram") {
            description("Author")
        }
        dependencies {
            dependency("spongeapi") {
                loadOrder(PluginDependency.LoadOrder.AFTER)
                optional(false)
            }
            dependency("packetevents") {
                loadOrder(PluginDependency.LoadOrder.AFTER)
                version("2.5.1-SNAPSHOT")
                optional(false)
            }
        }
    }
}
