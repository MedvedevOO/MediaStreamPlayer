plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    namespace = "com.example.musicplayer"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.constraintlayout.compose)
    implementation(libs.palette.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    // Lifecycle
    implementation(libs.lifecycle.extensions)
    implementation(libs.bundles.lifecycle)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Room
    ksp(libs.room.compiler)
    implementation(libs.room.common)
    implementation(libs.room.ktx)

    //gson
    implementation (libs.gson)

    // Coil
    implementation(libs.coil)
    implementation (libs.coil.gif)

    //Accompanist
    implementation(libs.bundles.accompanist)
    implementation(libs.accompanist.permissions)
    // Dagger - Hilt
    implementation(libs.bundles.hilt)
    implementation(libs.media3.ui)
    implementation(libs.material3.android)
    implementation(libs.datastore.core.android)
    implementation(libs.datastore.preferences)
    ksp(libs.bundles.hilt.ksp)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // ExoPlayer
//    api(libs.bundles.exoplayer)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation (libs.media3.exoplayer.hls)

    //retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
}