import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.muhammad.benchmark"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
    testOptions.managedDevices.allDevices {
        create("pixel6Api34", ManagedVirtualDevice::class.java) {
            device = "Pixel 6"
            apiLevel = 34
            systemImageSource = "google"
        }
    }
}
//baselineProfile {
//    managedDevices += "pixel6Api34"
//    skipBenchmarksOnEmulator = true
//    useConnectedDevices = true
//}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}