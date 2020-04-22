package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category

interface CategoryCallback {
    interface OneCategory {
        fun onResponse(category: Category)
        fun onError(message: String)
    }
}