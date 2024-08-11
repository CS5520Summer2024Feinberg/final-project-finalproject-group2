import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // google service
    alias(libs.plugins.google.services)
}

android {
    namespace = "edu.northeastern.group2final"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.northeastern.group2final"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
            val apiKey = localProperties.getProperty("OPENAI_API_KEY") ?: ""
            buildConfigField("String", "OPENAI_API_KEY", "\"$apiKey\"")
        } else {
            buildConfigField("String", "OPENAI_API_KEY", "\"\"")
        }
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
        dataBinding = true
        buildConfig = true
    }

}

dependencies {
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)

    // firestore
    implementation("com.google.firebase:firebase-firestore:24.4.1")

    // navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.play.services.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
//    implementation("com.github.igalata:Bubble-Picker:v0.2.4")
    // Solving Duplicate Class Error
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("com.google.android.gms:play-services-location:21.0.1")
}