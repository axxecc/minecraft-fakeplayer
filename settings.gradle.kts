rootProject.name = "fakeplayer-parent"

include(
        "fakeplayer-api",
        "fakeplayer-core",
        "fakeplayer-dist",
        "fakeplayer-v1_21_8",
        "fakeplayer-v1_21_11",
        "fakeplayer-v26_1"
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}