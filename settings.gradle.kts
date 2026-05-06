pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Music Playe"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":feature:home")
include(":feature:library")
include(":feature:detail")
include(":feature:search")
include(":feature:player")
include(":feature:playlist")
include(":feature:radio")
include(":feature:settings")
