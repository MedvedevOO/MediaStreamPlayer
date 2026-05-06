
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {

    compileSdk = 37
    defaultConfig {
        applicationId = "com.bearzwayne.musicplayer"
        minSdk = 30
        targetSdk = 37
        versionCode = 10
        versionName = "1.0.27"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    namespace = "com.bearzwayne.musicplayer"
    compileSdkMinor = 0
}

dependencies {

    // Modules
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":feature:home"))
    implementation(project(":feature:library"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:search"))
    implementation(project(":feature:player"))
    implementation(project(":feature:playlist"))
    implementation(project(":feature:radio"))
    implementation(project(":feature:settings"))

    implementation(libs.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.constraintlayout.compose)
    implementation(libs.palette.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.media3.ui)
    implementation(libs.material3.android)
    implementation(libs.datastore.core.android)
    implementation(libs.datastore.preferences)

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

    // Dagger - Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.ksp)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // ExoPlayer
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation (libs.media3.exoplayer.hls)

    //retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)

    // Testing
    androidTestImplementation(libs.ultron.android)
    androidTestImplementation(libs.ultron.allure)
    androidTestImplementation(libs.ultron.compose)
    
    // Mockito для тестирования
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
    // Mockito для Kotlin
    androidTestImplementation(libs.mockito.kotlin)
}