package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media

interface MediaCallback {
    interface OneMedia {
        fun onResponse(media: Media)
        fun onError(message: String)
        fun onLoading()
    }
}