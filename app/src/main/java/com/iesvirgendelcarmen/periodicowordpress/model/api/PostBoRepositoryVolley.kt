package com.iesvirgendelcarmen.periodicowordpress.model.api

import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.reflect.TypeToken
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.GsonSingleton
import com.iesvirgendelcarmen.periodicowordpress.model.VolleySingleton
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostMapper
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Post
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.User

object PostBoRepositoryVolley {
    fun readPostBoById(id: Int, callback: PostBoCallback.OnePostBO) {
        callback.onLoading()

        getPostById(id, object: ObjectsRequest {
            override fun onCompleted(
                post: Post?,
                media: Media?,
                user: User?,
                categories: List<Category>?
            ) {
                val postBO = PostMapper.transformDTOtoBO(post!!, media!!, user!!, categories!!)
                callback.onResponse(postBO)
            }

            override fun onError(message: String) {
                callback.onError(message)
            }
        })
    }


    private fun getPostById(postDtoId: Int, callback: ObjectsRequest) {
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            "${Endpoint.POSTS_URL}$postDtoId",
            Response.Listener
            {
                val post = GsonSingleton.getInstance().fromJson<Post>(it, Post::class.java)
                getPostAdditionalObjects(post, callback)
            },
            Response.ErrorListener
            {
                callback.onError("Post Error: " + it.message)
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }




    var media: Media? = null
    var author: User? = null
    var categories: List<Category>? = null
    var postDTO: Post? = null

    private fun getPostAdditionalObjects(post : Post, callback: ObjectsRequest) {
        postDTO = post

        requestMediaObject(post, callback)
        requestUserObject(post, callback)
        requestCategoriesObjects(post, callback)
    }

    private fun requestCategoriesObjects(post: Post, callback: ObjectsRequest) {
        var categoriesIdsList = ""
        for (category in post.categories)
            categoriesIdsList += "$category,"

        VolleySingleton.getInstance().requestQueue

        val stringRequestCategories = StringRequest(
            Request.Method.GET,
            "${Endpoint.CATEGORIES_URL}${Endpoint.INCLUDE}$categoriesIdsList",
            Response.Listener
            {
                val listType = object : TypeToken<List<Category>>() {}.type
                categories = GsonSingleton.getInstance().fromJson<List<Category>>(it, listType)
                sendPostObjects(callback)
            },
            Response.ErrorListener {
                callback.onError("Categories Error: " + it.message)
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequestCategories)
    }

    private fun requestUserObject(post: Post, callback: ObjectsRequest) {
        VolleySingleton.getInstance().requestQueue

        val stringRequestAuthor = StringRequest(
            Request.Method.GET,
            "${Endpoint.USERS_URL}${post.author}",
            Response.Listener
            {
                author = GsonSingleton.getInstance().fromJson<User>(it, User::class.java)
                sendPostObjects(callback)
            },
            Response.ErrorListener {
                callback.onError("User Error: " + it.message)
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequestAuthor)
    }

    private fun requestMediaObject(post: Post, callback: ObjectsRequest) {
        VolleySingleton.getInstance().requestQueue

        val stringRequestMedia = StringRequest(
            Request.Method.GET,
            "${Endpoint.MEDIA_URL}${post.featuredMedia}",
            Response.Listener
            {
                media = GsonSingleton.getInstance().fromJson<Media>(it, Media::class.java)
                sendPostObjects(callback)
            },
            Response.ErrorListener {
                callback.onError("Media Error: " + it.message)
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequestMedia)
    }

    private fun sendPostObjects(callback: ObjectsRequest) {
        if (media != null && author != null && categories != null) {
            callback.onCompleted(postDTO, media, author, categories)
        }
    }

    private interface ObjectsRequest {
        fun onCompleted(post: Post?, media: Media?, user: User?, categories: List<Category>?)
        fun onError(message :String)
    }
}