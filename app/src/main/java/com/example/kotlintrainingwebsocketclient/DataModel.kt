package com.example.kotlintrainingwebsocketclient

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataModel(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return "id = ${id}; name = ${name};"
    }
}