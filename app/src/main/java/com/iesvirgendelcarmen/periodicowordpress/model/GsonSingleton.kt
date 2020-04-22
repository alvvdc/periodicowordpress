package com.iesvirgendelcarmen.periodicowordpress.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GsonSingleton {
    companion object {
        private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

        fun getInstance(): Gson {
            return gson
        }
    }
}