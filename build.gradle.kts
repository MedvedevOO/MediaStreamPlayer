buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
    }


}
plugins {
    alias(libs.plugins.com.google.devtools.ksp) apply false

}
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}