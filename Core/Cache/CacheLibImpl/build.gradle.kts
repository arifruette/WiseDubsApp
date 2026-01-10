plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinKapt)
}

android {
    namespace = "ru.ari.cache"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.dagger)
    kapt(libs.daggerCompiler)

    api(projects.cacheLibApi)

    // region Datastore

    implementation(libs.androidx.datastore.preferences)

    // endregion
}