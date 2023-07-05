@file:Suppress("unused")


private object Versions {
    const val retrofit_version = "2.9.0"
    const val room_version = "2.4.3"
    // 根目录的 build.gradle 里面的 hilt 插件版本也需要一起改动
    const val hilt_version = "2.44"
    const val glide_version = "4.11.0"
    const val exoplayer_version = "1.0.1"
}

object SDKVersion {
    const val sdkVersion = 33
    const val minSdkVersion = 21
    const val targetSdkVersion = 33
}

object QfhDependencies {

    // test library
    const val junit = "junit:junit:4.13.2"
    const val android_junit = "androidx.test.ext:junit:1.1.3"
    const val android_espresso_core = "androidx.test.espresso:espresso-core:3.4.0"

    // base library
    const val core_ktx = "androidx.core:core-ktx:1.8.0"
    const val appcompat = "androidx.appcompat:appcompat:1.6.1"
    const val material = "com.google.android.material:material:1.5.0"
    const val fragment_ktx = "androidx.fragment:fragment-ktx:1.5.5"
    const val activity_ktx = "androidx.activity:activity-ktx:1.6.1"

    // 协程
    const val kotlinx_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"
    const val kotlinx_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1"

    // 协程作用域对各个架构组件的支持
    const val lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"
    const val lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
    const val lifecycle_livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:2.5.1"

    // retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    const val okHttp = "com.squareup.okhttp3:okhttp:4.10.0"
    const val converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit_version}"

    // room
    const val room_runtime = "androidx.room:room-runtime:${Versions.room_version}"
    const val room_compiler = "androidx.room:room-compiler:${Versions.room_version}"

    // room 和 flow 配合使用
    const val room_ktx = "androidx.room:room-ktx:${Versions.room_version}"

    // 依赖注入 hilt
    const val hilt_android = "com.google.dagger:hilt-android:${Versions.hilt_version}"
    const val hilt_android_compiler =
        "com.google.dagger:hilt-compiler:${Versions.hilt_version}"

    // glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide_version}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide_version}"

    // exoplayer
    const val exoplayer_core = "androidx.media3:media3-exoplayer:${Versions.exoplayer_version}"
    const val exoplayer_ui = "androidx.media3:media3-ui:${Versions.exoplayer_version}"
    const val exoplayer_rtmp ="androidx.media3:media3-datasource-rtmp:${Versions.exoplayer_version}"


    // tv 开发
    const val leanback = "androidx.leanback:leanback:1.2.0-alpha02"
    const val leanback_paging = "androidx.leanback:leanback-paging:1.1.0-alpha09"
    const val leanback_tab = "androidx.leanback:leanback-tab:1.1.0-beta01"
}