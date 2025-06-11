
plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.muhammad.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
androidComponents {
    beforeVariants(selector().all()) { variant ->
        variant.enableAndroidTest = false
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(project(":core:util"))
    implementation(libs.androidx.app.startup)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.androidx.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.compose.http)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.firebase.analytics) {
        exclude(group = "com.google.guava")
    }
    implementation(libs.firebase.app.check)
    implementation(libs.firebase.config)
    implementation(libs.firebase.config.ktx)
    androidTestImplementation(project(":core:testing"))
}