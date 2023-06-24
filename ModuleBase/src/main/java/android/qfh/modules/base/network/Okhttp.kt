package android.qfh.modules.base.network

import android.qfh.modules.base.BuildConfig
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val baseOkHttpClient = OkHttpClient.Builder()
    .addInterceptor(
        HttpLoggingInterceptor {
            Log.d("okHttpClient", it)
        }.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.NONE else HttpLoggingInterceptor.Level.NONE)
    )
    .build()