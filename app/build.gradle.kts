plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.reservasmedicasmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reservasmedicasmobile"
        minSdk = 22
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.volley)
    implementation("androidx.fragment:fragment:fragment_version")
    implementation("com.google.android.gms:play-services-safetynet:18.1.0")
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.material.v140)
    implementation(libs.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}