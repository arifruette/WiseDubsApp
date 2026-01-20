plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinKapt)
}

val baseApiUrl = (findProperty("baseApiUrl") ?: System.getenv("BASE_API_URL") ?: "NO_FOUND")

android {
    namespace = "ru.ari.wisedubsapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ru.ari.wisedubsapp"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_API_URL", "\"$baseApiUrl\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(projects.navigationImpl)
    implementation(projects.navigationEntriesLib)
    implementation(projects.diCoreLib)
    implementation(projects.cacheLibImpl)
    implementation(projects.networkLibImpl)
    implementation(projects.composeCoreLib)
    implementation(projects.login)
    implementation(projects.sharing)

    implementation(libs.dagger)
    kapt(libs.daggerCompiler)

    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.androidx.core.splash)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}