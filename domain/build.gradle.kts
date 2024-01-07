plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(project(":common-kotlin"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}