plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.com.google.devtools.ksp)
}

android {
    namespace = "com.bearzwayne.musicplayer.domain"
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
    implementation(libs.core.ktx)
    implementation(libs.kotlinx.serialization.json)
    // Room annotations for @Entity models
    implementation(libs.room.common)
    // Hilt for @Inject on use cases
    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.ksp)
    // Coroutines for suspend functions and Flow
    implementation(libs.bundles.coroutines)
}
