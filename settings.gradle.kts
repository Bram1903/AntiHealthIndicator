plugins {
    id("com.gradle.enterprise") version("3.16.2")
}

include("common")
include("spigot")

rootProject.name = "AntiHealthIndicator"

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
