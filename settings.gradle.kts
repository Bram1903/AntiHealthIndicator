plugins {
    id("com.gradle.enterprise") version ("3.16.2")
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
include("common")
include("platforms:spigot")
