package com.iesvirgendelcarmen.periodicowordpress.config

class Endpoint {
    companion object {

        //private const val BASE_URL = "http://192.168.1.8:8080/index.php?rest_route=/wp/v2/"

        private const val BASE_URL = "https://time.com/index.php?rest_route=/wp/v2/"
        //private const val BASE_URL = "https://newyork.cbslocal.com/index.php?rest_route=/wp/v2/"
        //private const val BASE_URL = "https://chinadigitaltimes.net/index.php?rest_route=/wp/v2/"

        private const val POSTS = "posts"
        const val POSTS_URL = "$BASE_URL$POSTS/"

        private const val CATEGORIES = "categories"
        const val CATEGORIES_URL = "$BASE_URL$CATEGORIES/"

        private const val MEDIA = "media"
        const val MEDIA_URL = "$BASE_URL$MEDIA/"

        private const val USERS = "users"
        const val USERS_URL = "$BASE_URL$USERS/"

        const val PAGE = "&page="
        const val PER_PAGE = "&per_page="
        const val DEFAULT_PER_PAGE = 10

        const val CATEGORIES_OPTION = "&categories="
        const val INCLUDE = "&include="

        private const val ORDER_BY = "&orderby="
        private const val ORDER = "&order="
        private const val DESC = "desc"
        private const val COUNT = "count"
        const val ORDER_BY_COUNT = "$ORDER_BY$COUNT"
        const val ORDER_DESC = "$ORDER$DESC"
    }
}