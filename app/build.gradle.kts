plugins {
    // choose ONE of the two styles:

    // — A. via Version‑Catalog aliases (cleanest if you already have libs.versions.toml):
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

    /* — B. or the explicit ids:
    id("com.android.application")
    id("com.google.gms.google-services")
    */
}

android {
    namespace   = "com.example.b07demosummer2024"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.example.b07demosummer2024"
        minSdk        = 24
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { viewBinding = true }
}

dependencies {
    // Jetpack
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")


    // --- Firebase: keep ONE BoM line ---
    implementation(platform(libs.firebase.bom))    // e.g. 34.0.0 from toml
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    // Google Identity / Credentials
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // Other
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.google.firebase.database)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
