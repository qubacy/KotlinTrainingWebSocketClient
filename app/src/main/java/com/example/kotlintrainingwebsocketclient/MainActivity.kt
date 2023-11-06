package com.example.kotlintrainingwebsocketclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val mModel: MainViewModel by viewModels {
        MainViewModelFactory((application as Application).moshi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            mModel.dataFlow.collect {
                if (it == null) return@collect

                Log.d(TAG, "data: ${it.toString()}")
            }
        }
    }

    override fun onStart() {
        super.onStart()

        mModel.initStompClient()
    }
}