package com.example.kotlintrainingwebsocketclient

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.lang.IllegalArgumentException
import kotlin.coroutines.CoroutineContext

class MainViewModel(
    val moshi: Moshi
) : ViewModel() {
    companion object {
        const val TAG = "MainViewModel"

        const val BASE_WS_URL = "wss://ws.postman-echo.com/raw/"

        const val BASE_WS_TOPIC = ""
    }

    private lateinit var mOkHttpClient: OkHttpClient
    private lateinit var mWebSocket: WebSocket

    private val mDataFlow = MutableStateFlow<DataModel?>(null)
    val dataFlow: StateFlow<DataModel?> = mDataFlow

    fun initStompClient() {
        mOkHttpClient = OkHttpClient.Builder().build()

        val request = Request.Builder().url(BASE_WS_URL + BASE_WS_TOPIC).build()
        val dataModelAdapter = moshi.adapter(DataModel::class.java)
        val listener = MainWebSocketListener(
            dataModelAdapter, mDataFlow, viewModelScope.coroutineContext)

        mWebSocket = mOkHttpClient.newWebSocket(request, listener)
    }

    class MainWebSocketListener(
        private val dataModelAdapter: JsonAdapter<DataModel>,
        private val mDataFlow: MutableStateFlow<DataModel?>,
        private val coroutineContext: CoroutineContext
    ) : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)

            Log.d(TAG, "onOpen()")

            CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                val dataModel = DataModel(0, "test")
                val dataModelString = dataModelAdapter.toJson(dataModel)

                webSocket.send(dataModelString)

                Log.d(TAG, "onOpen(): sent: $dataModelString")
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)

            Log.d(TAG, "onMessage()")

            CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
                Log.d(TAG, "onMessage(): received: $text")

                val dataModel = dataModelAdapter.fromJson(text)

                mDataFlow.emit(dataModel)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)

            val dataModelString = bytes.string(Charsets.UTF_8)

            onMessage(webSocket, dataModelString)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)

            Log.d(TAG, "onFailure()")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)

            Log.d(TAG, "onClosed()")
        }
    }
}

class MainViewModelFactory(
    val moshi: Moshi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MainViewModel::class.java))
            throw IllegalArgumentException()

        return MainViewModel(moshi) as T
    }
}