plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.hiltAndroidGradle)
    alias(libs.plugins.parcelizePlugin)
}

android {
    namespace = "com.fiz.battleinthespace.database"
}

dependencies {

    implementation(project(":common-android"))
    implementation(project(":common-kotlin"))
    implementation(project(":domain"))

    implementation(libs.core.ktx)

    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.play.services.auth)
    implementation(libs.firebase.ui.auth)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}