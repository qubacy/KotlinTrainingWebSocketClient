package com.example.kotlintrainingwebsocketclient

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Application : Application() {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}