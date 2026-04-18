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
include(":PostsApi")
include(":PostsImpl")
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
include(":MyPostsApi")
include(":MyPostsImpl")
include(":ManagePostApi")
include(":ManagePostImpl")

project(":NavigationApi").projectDir = file("Core/Navigation/NavigationApi")
project(":NavigationImpl").projectDir = file("Core/Navigation/NavigationImpl")
project(":Login").projectDir = file("Feature/Auth/Login")
project(":SharingApi").projectDir = file("Feature/Sharing/SharingApi")
project(":SharingImpl").projectDir = file("Feature/Sharing/SharingImpl")
project(":PostsApi").projectDir = file("Feature/Posts/PostsApi")
project(":PostsImpl").projectDir = file("Feature/Posts/PostsImpl")
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
project(":MyPostsApi").projectDir = file("Feature/MyPosts/MyPostsApi")
project(":MyPostsImpl").projectDir = file("Feature/MyPosts/MyPostsImpl")
project(":ManagePostApi").projectDir = file("Feature/ManagePost/ManagePostApi")
project(":ManagePostImpl").projectDir = file("Feature/ManagePost/ManagePostImpl")
