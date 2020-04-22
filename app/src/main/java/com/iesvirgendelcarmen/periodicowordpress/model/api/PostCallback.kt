package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Post

interface PostCallback {
    interface ListPosts {
        fun onResponse(posts: List<Post>)
        fun onError(message: String)
        fun onLoading()
    }
}