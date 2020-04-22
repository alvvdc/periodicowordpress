package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.User

interface UserCallback {
    interface OneUser {
        fun onResponse(user: User)
        fun onError(message: String)
        fun onLoading()
    }
}