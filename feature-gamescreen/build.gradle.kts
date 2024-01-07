plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.hiltAndroidGradle)
}

android {
    namespace = "com.fiz.battleinthespace.feature_gamescreen"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":common-android"))
    implementation(project(":common-kotlin"))
    implementation(project(":feature-game"))

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.firebase.bom))
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth")
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-database-ktx")
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-storage")
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.play.services.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}