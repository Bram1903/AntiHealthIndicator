pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version ("3.16.2")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

rootProject.name = "AntiHealthIndicator"
include(":api")
include(":common")
include(":platforms:bukkit")
include(":platforms:velocity")
include(":platforms:bungeecord")
include(":platforms:sponge")
include(":tests:api-bukkit-test-plugin")
include(":tests:api-velocity-test-plugin")
