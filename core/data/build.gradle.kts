plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.com.google.devtools.ksp)
}

android {
    namespace = "com.bearzwayne.musicplayer.data"
    compileSdk = 37
    defaultConfig {
        minSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.core.ktx)
    // Room - full setup
    implementation(libs.room.common)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    // Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer.hls)
    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core.android)
    // Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.ksp)
    // Coroutines
    implementation(libs.bundles.coroutines)
    // Gson
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
}
