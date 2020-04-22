package com.iesvirgendelcarmen.periodicowordpress.model

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.iesvirgendelcarmen.periodicowordpress.App

class VolleySingleton {
    companion object {
        @Volatile
        private var INSTANCE :VolleySingleton? = null

        fun getInstance() :VolleySingleton {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton().also {
                    INSTANCE = it
                }
            }
        }
    }

    val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(App.getContext())
    }

    fun <T> addToRequestQueue(request : Request<T>) {
        requestQueue.add(request)
    }
}