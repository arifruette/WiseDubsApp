pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "WiseDubsApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


include(":app")
include(":NavigationApi")
include(":NavigationImpl")
include(":Login")
include(":Sharing")

project(":NavigationApi").projectDir = file("Core/Navigation/NavigationApi")
project(":NavigationImpl").projectDir = file("Core/Navigation/NavigationImpl")
project(":Login").projectDir = file("Feature/Auth/Login")
project(":Sharing").projectDir = file("Feature/Sharing")
