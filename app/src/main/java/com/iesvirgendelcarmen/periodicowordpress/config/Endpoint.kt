package com.iesvirgendelcarmen.periodicowordpress.config

class Endpoint {
    companion object {

        val BASE_URL = "http://192.168.1.8:8080/index.php?rest_route=/wp/v2/"

        val POSTS = "posts"
        val POSTS_URL = BASE_URL + POSTS

        val CATEGORIES = "categories"
        val CATEGORY_URL = "$BASE_URL$CATEGORIES/"

        val MEDIA = "media"
        val MEDIA_URL = "$BASE_URL$MEDIA"
    }
}