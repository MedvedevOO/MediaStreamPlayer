plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.com.google.devtools.ksp)
}

android {
    namespace = "com.bearzwayne.musicplayer.ui"
    compileSdk = 37
    defaultConfig {
        minSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Expose core modules to all feature modules via api
    api(project(":core:domain"))
    api(project(":core:data"))
    // Expose Compose foundation so feature modules don't repeat these deps
    api(platform(libs.compose.bom))
    api(libs.bundles.compose)
    api(libs.material3.android)
    api(libs.navigation.compose)
    api(libs.coil)
    api(libs.coil.gif)
    api(libs.bundles.accompanist)
    api(libs.bundles.lifecycle)
    api(libs.bundles.coroutines)
    api(libs.kotlinx.serialization.json)
    api(libs.datastore.preferences)
    api(libs.datastore.core.android)
    // Non-exposed implementation deps
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.constraintlayout.compose)
    implementation(libs.palette.ktx)
    implementation(libs.media3.ui)
    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.ksp)
}
