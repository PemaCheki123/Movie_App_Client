plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mobile.movie_project_one"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mobile.movie_project_one"
        minSdk = 24
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.viewpager2)   //this is used for fragment and view
    implementation(libs.glide)  //it is used for image loading and caching with minimal code
    implementation(libs.chip.navigation.bar)  //navigation bar using chip-style UI element
    implementation(libs.blurview)        // used for creating blur effect on image and video
    implementation (libs.circleimageview)  //making image to circular
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")



}


