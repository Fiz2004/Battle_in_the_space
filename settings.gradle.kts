pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

buildCache {
    local {
        isEnabled = true
    }
}

rootProject.name = "Battle in the space"
include(":app")
include(":feature-mainscreen")
include(":feature-gamescreen")
include(":data")
include(":common-android")
include(":domain")
include(":feature-game")
include(":common-kotlin")
