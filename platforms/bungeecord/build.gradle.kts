plugins {
    antihealthindicator.`java-conventions`
}

repositories {
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven("https://repo.papermc.io/repository/maven-public/") // For Brigadier
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.bungeecord)
    compileOnly(libs.packetevents.bungeecord)
}
