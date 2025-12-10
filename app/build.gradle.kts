
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
val unsplashApiKey = properties.getProperty("Unsplash_Api_Key") ?: ""

android {
    namespace = "com.hanpro.prographyproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hanpro.prographyproject"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "UNSPLASH_KEY", unsplashApiKey)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit, OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // accompanist - 시스템 ui
    implementation(libs.accompanist.systemuicontroller)

    // coil - 이미지 로더
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // shimmer
    implementation(libs.compose.shimmer)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.ktx) 
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Test
    testImplementation(libs.junit)                      // 자바 기본 테스트 라이브러리
    testImplementation(libs.mockk)                      // 코틀린용 모킹 라이브러리
    testImplementation(libs.kotlinx.coroutines.test)    // 코루틴 테스트용 - runTest 등
}

kapt {
    correctErrorTypes = true
}