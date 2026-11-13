plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.callapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.callapp"
        minSdk = 29
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("androidx.core:core-ktx:1.13.1")
}
