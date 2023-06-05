plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.fiz.battleinthespace.common"
}

dependencies {

    implementation(libs.material)

    implementation(libs.lifecycle.runtime.ktx)

}
