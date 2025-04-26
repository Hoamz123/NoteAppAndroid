plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.hoamz.hoamz"
    compileSdk = 35

    viewBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.hoamz.hoamz"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("de.hdodenhof:circleimageview:3.1.0")


    //room
    implementation ("androidx.room:room-runtime:2.2.5")
    implementation ("androidx.room:room-rxjava2:2.2.5")
    annotationProcessor ("androidx.room:room-compiler:2.2.5")

    //calender
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")


    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}