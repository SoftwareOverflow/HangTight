import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val localProperties = Properties()
try {
    localProperties.load(FileInputStream(rootProject.file("local.properties")))
} catch (_ : Exception) {
    logger.warn("No Local Properties File Found!")
}

android {
    namespace = "com.softwareoverflow.hangtight"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.newtonapps.hangtight"
        minSdk = 27
        targetSdk = 35
        versionCode = 45
        versionName = "2.7.4"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("compose-destinations.codeGenPackageName", "com.softwareoverflow.hangtight.ui.screen")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        buildConfigField("String", "BASE64_ENCODED_PUBLIC_KEY",
            "\"${localProperties["base64EncodedPublicKey"]}\""
        )
        buildConfigField("String[]", "DEV_DEVICES", "new String[] ${localProperties["testDeviceIds"]}")
    }

    buildTypes {
        debug {
            isDebuggable = true
            resValue("string", "adUnitId_banner", "\"ca-app-pub-3940256099942544/6300978111\"")
            resValue("string", "adUnitId_interstitial", "\"ca-app-pub-3940256099942544/1033173712\"")
        }

        release {
            isMinifyEnabled =  true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            resValue("string", "adUnitId_banner", "\"ca-app-pub-5961771507160254/4534904901\"")
            resValue("string", "adUnitId_interstitial", "\"ca-app-pub-5961771507160254/2482816006\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val nav_version = "2.5.3"
    val roomVersion = "2.8.2"
    val coroutinesVersion = "1.10.2"
    val composeVersion = "1.5.0"
    val hiltVersion = "2.57.2"

    implementation("androidx.core:core-ktx:1.16.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.2.0"))
    implementation(platform("androidx.compose:compose-bom:2025.09.01"))

    //implementation("org.jetbrains.kotlin:kotlin-compose-compiler-plugin-embeddable:2.2.0")
    implementation("org.jetbrains.kotlin:kotlin-compose-compiler-plugin:2.2.20")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")


    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")
    implementation("androidx.navigation:navigation-compose")
    implementation("androidx.activity:activity-compose")
    //implementation("androidx.navigation:navigation-compose")
    //implementation("androidx.navigation:navigation-ui-ktx")

    implementation("androidx.preference:preference:1.2.1")

    implementation("com.google.android.gms:play-services-ads:24.7.0")

    // Monetization
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")

    debugImplementation("androidx.compose.ui:ui-tooling")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Firebase (with BoM)
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // In-app reviews
    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.play:review-ktx:2.0.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Room databases
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // ComposeDestinations library dependencies
    implementation("io.github.raamcosta.compose-destinations:core:2.2.0")
    ksp("io.github.raamcosta.compose-destinations:ksp:2.2.0")

    // Hilt DI dependencies
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("com.google.android.ump:user-messaging-platform:3.2.0")
}