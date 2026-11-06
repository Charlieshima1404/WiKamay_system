// PURPOSE: Tells Gradle where to download plugins/libraries and which modules belong to the project.
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// EDITABLE: The project name shown in Android Studio.
rootProject.name = "WiKamay"
include(":app")
