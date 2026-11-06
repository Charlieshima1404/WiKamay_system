// PURPOSE: Build configuration for the WiKamay app module (Kotlin + Jetpack Compose).

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // PURPOSE: Unique package name of the app. Must match the folder structure in app/src/main/java.
    namespace = "com.wikamay.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wikamay.app"
        // EDITABLE: Oldest Android version supported (24 = Android 7.0).
        minSdk = 24
        targetSdk = 34
        // EDITABLE: Version shown to users / used for updates.
        versionCode = 1
        versionName = "1.0-ui-prototype"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // PURPOSE: Turns on Jetpack Compose (our approved UI framework).
    buildFeatures {
        compose = true
    }
    composeOptions {
        // NOTE: This version must match the Kotlin version (1.9.24) declared in the root build.gradle.kts.
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // PURPOSE: The BOM keeps all Compose library versions compatible with each other.
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // PURPOSE: Extra icons (microphone, speaker, video camera, ...).
    implementation("androidx.compose.material:material-icons-extended")

    // PURPOSE: Moves between screens (see navigation/WiKamayNavGraph.kt).
    implementation("androidx.navigation:navigation-compose:2.7.7")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
