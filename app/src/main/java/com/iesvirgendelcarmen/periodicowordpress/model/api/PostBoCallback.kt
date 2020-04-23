package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO

interface PostBoCallback {
    interface OnePostBO {
        fun onResponse(postBO: PostBO)
        fun onError(message: String)
        fun onLoading()
    }
}