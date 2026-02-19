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
include(":SharingApi")
include(":SharingImpl")
include(":DICoreLib")
include(":CacheLibApi")
include(":CacheLibImpl")
include(":ComposeCoreLib")
include(":NetworkLibApi")
include(":NetworkLibImpl")
include(":DesignSystemCoreLib")
include(":Registration")
include(":AuthCommonLibApi")
include(":AuthCommonLibImpl")

project(":NavigationApi").projectDir = file("Core/Navigation/NavigationApi")
project(":NavigationImpl").projectDir = file("Core/Navigation/NavigationImpl")
project(":Login").projectDir = file("Feature/Auth/Login")
project(":SharingApi").projectDir = file("Feature/SharingApi")
project(":SharingImpl").projectDir = file("Feature/SharingImpl")
project(":DICoreLib").projectDir = file("Core/DI/DICoreLib")
project(":CacheLibApi").projectDir = file("Core/Cache/CacheLibApi")
project(":CacheLibImpl").projectDir = file("Core/Cache/CacheLibImpl")
project(":ComposeCoreLib").projectDir = file("Core/ComposeCoreLib")
project(":NetworkLibApi").projectDir = file("Core/Network/NetworkLibApi")
project(":NetworkLibImpl").projectDir = file("Core/Network/NetworkLibImpl")
project(":DesignSystemCoreLib").projectDir = file("Core/DesignSystemCoreLib")
project(":Registration").projectDir = file("Feature/Auth/Registration")
project(":AuthCommonLibApi").projectDir = file("Feature/Auth/AuthCommonLibApi")
project(":AuthCommonLibImpl").projectDir = file("Feature/Auth/AuthCommonLibImpl")

