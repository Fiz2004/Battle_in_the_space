plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.fiz.battleinthespace.domain"
}

dependencies {
    implementation(project(":common"))

    implementation(libs.kotlinx.coroutines.core)
}