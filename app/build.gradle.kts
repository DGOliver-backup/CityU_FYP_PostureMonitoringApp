plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.o1mlkit4"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.o1mlkit4"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.espresso.core)
    implementation(libs.pose.detection)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // CameraX dependencies
    implementation ("androidx.camera:camera-core:1.2.3")
    implementation ("androidx.camera:camera-camera2:1.2.3")
    implementation ("androidx.camera:camera-lifecycle:1.2.3")
    implementation ("androidx.camera:camera-view:1.2.3")

    // ML Kit Pose Detection

    //implementation ("com.google.mlkit:pose-detection:17.0.0")
    //implementation ("com.google.mlkit:pose-detection-accurate:17.0.0")

    implementation ("com.google.mlkit:pose-detection-accurate:18.0.0-beta5")

    // For the accurate model (optional):
    // implementation "com.google.mlkit:pose-detection-accurate:17.0.0"

    // Kotlin coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(libs.coil.compose) // Replace with the latest version



    //// newlly added
    implementation (libs.androidx.camera.video.v130)

    // ExoPlayer for internal video playback
    implementation ("androidx.media3:media3-exoplayer:1.0.2")
    implementation ("androidx.media3:media3-ui:1.0.2")
    // Other
    implementation ("com.google.code.gson:gson:2.10.1")


    // hweww ia nwq
    implementation (libs.accompanist.pager)

    // In your build.gradle (module) dependencies section, add:
    implementation ("androidx.compose.material:material-icons-extended:<latest_version>")


    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.28.0")
    implementation (libs.androidx.core.ktx.v1120)
    implementation ("androidx.media3:media3-exoplayer:1.3.0")
    implementation ("androidx.media3:media3-ui:1.3.0")
    implementation ("androidx.media3:media3-common:1.3.0")


}