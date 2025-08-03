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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("de.hdodenhof:circleimageview:3.1.0")


    // Room core
    implementation ("androidx.room:room-runtime:2.7.2")
    annotationProcessor ("androidx.room:room-compiler:2.7.2")


    implementation ("androidx.room:room-rxjava3:2.7.2")

    // Optional nếu có xài Paging
    implementation ("androidx.room:room-paging:2.7.2")

    // Optional test
    testImplementation ("androidx.room:room-testing:2.7.2")

    //calender
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")


    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    //cameraX
    // CameraX core library
    implementation("androidx.camera:camera-core:1.4.2")
// CameraX Camera2 extensions
    implementation("androidx.camera:camera-camera2:1.4.2")
// CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:1.4.2")
// CameraX Viewfinder (hiển thị preview - phiên bản beta mới nhất)
    implementation("androidx.camera:camera-viewfinder:1.3.0-beta02")
// CameraX View (UI cao cấp)
    implementation("androidx.camera:camera-view:1.4.2")

    implementation("androidx.work:work-runtime:2.9.1")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //photoview
    implementation("com.github.chrisbanes:PhotoView:2.3.0")


    //animation
    implementation("com.airbnb.android:lottie:6.6.7")

}