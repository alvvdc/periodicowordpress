package com.iesvirgendelcarmen.periodicowordpress.model.api

import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.reflect.TypeToken
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.GsonSingleton
import com.iesvirgendelcarmen.periodicowordpress.model.VolleySingleton
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MediaMapper
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostMapper
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.*
import java.lang.StringBuilder
import java.util.*

object PostBoRepositoryVolley {
    fun readPostBoById(id: Int, callback: PostBoCallback.OnePostBO) {
        callback.onLoading()

        getPostById(id, object: ObjectsRequest {
            override fun onCompleted(postBoObjects: PostBoObjects) {
                val mediaBO = MediaMapper.transformDTOtoBO(postBoObjects.media!!, postBoObjects.mediaAuthor!!)
                val postBO = PostMapper.transformDTOtoBO(postBoObjects.post, mediaBO, postBoObjects.author!!, postBoObjects.categories!!)
                callback.onResponse(postBO)
            }

            override fun onError(message: String) {
                callback.onError(message)
            }
        })
    }

    fun readPostsBo(callback: PostBoCallback.ListPostBO, page: Int = 1, category: Int = -1, posts: Array<Int>? = null) {
        getPostsBO(object: PostBoCallback.ListPostBO {
            override fun onResponse(postsBO: List<PostBO>) {
                callback.onResponse(postsBO)
            }

            override fun onError(message: String) {
                callback.onError(message)
            }

            override fun onLoading() {
                callback.onLoading()
            }
        }, page, category, posts)
    }


    private fun getPostById(postDtoId: Int, callback: ObjectsRequest) {
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            "${Endpoint.POSTS_URL}$postDtoId",
            Response.Listener
            {
                val post = GsonSingleton.getInstance().fromJson<Post>(it, Post::class.java)
                getPostAdditionalObjects(PostBoObjects(post), callback)
            },
            Response.ErrorListener
            {
                callback.onError("Post Error: " + it.message + " ${it.networkResponse.statusCode}  ${it.networkResponse.data.toString()}")
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }


    private fun getPostsBO(callback: PostBoCallback.ListPostBO, page: Int = 1, category: Int = -1, posts: Array<Int>? = null) {
        callback.onLoading()
        VolleySingleton.getInstance().requestQueue

        var postsIds = ""

        if (posts != null) {
            val stringBuilder = StringBuilder()
            posts.forEach { id -> stringBuilder.append("$id,") }
            postsIds = stringBuilder.toString()
        }

        val URL = if (category == -1 && posts == null)
            Endpoint.POSTS_URL + Endpoint.PER_PAGE + Endpoint.DEFAULT_PER_PAGE.toString() + Endpoint.PAGE + page.toString()
        else if (category == -1 && posts != null)
            Endpoint.POSTS_URL + Endpoint.PER_PAGE + Endpoint.DEFAULT_PER_PAGE.toString() + Endpoint.PAGE + page.toString() + Endpoint.INCLUDE + postsIds
        else
            Endpoint.POSTS_URL + Endpoint.PER_PAGE + Endpoint.DEFAULT_PER_PAGE.toString() + Endpoint.PAGE + page.toString() + Endpoint.CATEGORIES_OPTION + category

        Log.d("ALVARO", URL)

        val stringRequest = StringRequest(
            Request.Method.GET,
            URL,
            Response.Listener
            {
                val listType = object: TypeToken<List<Post>>() {}.type
                val posts = GsonSingleton.getInstance().fromJson<List<Post>>(it, listType)
                val postsBO = mutableListOf<PostBO>()


                for (post in posts) {

                    getPostAdditionalObjects(PostBoObjects(post), object: ObjectsRequest {
                        override fun onCompleted(postBoObjects: PostBoObjects) {

                            val mediaBO = MediaMapper.transformDTOtoBO(postBoObjects.media!!, postBoObjects.mediaAuthor!!)
                            val postBO = PostMapper.transformDTOtoBO(postBoObjects.post, mediaBO, postBoObjects.author!!, postBoObjects.categories!!)
                            postsBO.add(postBO)

                            if (postsBO.size >= posts.size) {
                                callback.onResponse(postsBO)
                            }
                        }

                        override fun onError(message: String) {
                            callback.onError("Posts BO Error: $message")
                        }

                    })
                }
            },
            Response.ErrorListener
            {
                callback.onError("Post Error: " + it.message)
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }




    private fun getPostAdditionalObjects(postBoObjects: PostBoObjects, callback: ObjectsRequest) {
        requestMediaObject(postBoObjects, callback)
        requestUserObject(postBoObjects, callback)
        requestCategoriesObjects(postBoObjects, callback)
    }

    private fun requestCategoriesObjects(postBoObjects: PostBoObjects, callback: ObjectsRequest) {
        var categoriesIdsList = ""
        for (category in postBoObjects.post.categories)
            categoriesIdsList += "$category,"

        VolleySingleton.getInstance().requestQueue

        val stringRequestCategories = StringRequest(
            Request.Method.GET,
            "${Endpoint.CATEGORIES_URL}${Endpoint.INCLUDE}$categoriesIdsList",
            Response.Listener
            {
                val listType = object : TypeToken<List<Category>>() {}.type
                postBoObjects.categories = GsonSingleton.getInstance().fromJson<List<Category>>(it, listType)
                sendPostObjects(postBoObjects, callback)
            },
            Response.ErrorListener {
                callback.onError("Categories Error: " + it.message + " ${it.networkResponse.statusCode}  ${it.networkResponse.data.toString()}")
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequestCategories)
    }

    private fun requestUserObject(postBoObjects: PostBoObjects, callback: ObjectsRequest) {
        VolleySingleton.getInstance().requestQueue

        val stringRequestAuthor = StringRequest(
            Request.Method.GET,
            "${Endpoint.USERS_URL}${postBoObjects.post.author}",
            Response.Listener
            {
                postBoObjects.author = GsonSingleton.getInstance().fromJson<User>(it, User::class.java)
                sendPostObjects(postBoObjects, callback)
            },
            Response.ErrorListener {
                callback.onError("User Error: " + it.message + " ${it.networkResponse.statusCode}  ${it.networkResponse.data.toString()}")
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequestAuthor)
    }

    private fun requestMediaObject(postBoObjects: PostBoObjects, callback: ObjectsRequest) {
        if (postBoObjects.post.featuredMedia > 0)
        {
            VolleySingleton.getInstance().requestQueue

            val stringRequestMedia = StringRequest(
                Request.Method.GET,
                "${Endpoint.MEDIA_URL}${postBoObjects.post.featuredMedia}",
                Response.Listener
                {
                    postBoObjects.media = GsonSingleton.getInstance().fromJson<Media>(it, Media::class.java)
                    requestMediaAuthorObject(postBoObjects, callback)
                    sendPostObjects(postBoObjects, callback)
                },
                Response.ErrorListener {
                    //callback.onError("Media Error: " + it.message + " ${it.networkResponse.statusCode}  ${it.networkResponse.data.toString()}")
                    postBoObjects.media = Media(-1, Date(), Date(), "", Rendered(""), -1, Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, "")
                    postBoObjects.mediaAuthor = User(-1, "", "", "", "", AvatarUrls("", "", ""))
                    sendPostObjects(postBoObjects, callback)
                    //Log.d("ALVARO", "Media Error: " + it.message + " ${it.networkResponse.statusCode}  ${it.networkResponse.data.toString()}")
                }
            )
            VolleySingleton.getInstance().addToRequestQueue(stringRequestMedia)
        }
        else
        {
            postBoObjects.media = Media(-1, Date(), Date(), "", Rendered(""), -1, Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, "")
            postBoObjects.mediaAuthor = User(-1, "", "", "", "", AvatarUrls("", "", ""))
            sendPostObjects(postBoObjects, callback)
        }
    }

    private fun requestMediaAuthorObject(postBoObjects: PostBoObjects, callback: ObjectsRequest) {
        VolleySingleton.getInstance().requestQueue

        val stringRequestMediaAuthor = StringRequest(
            Request.Method.GET,
            "${Endpoint.USERS_URL}${postBoObjects.media?.author}",
            Response.Listener {
                postBoObjects.mediaAuthor = GsonSingleton.getInstance().fromJson(it, User::class.java)
                sendPostObjects(postBoObjects, callback)
            },
            Response.ErrorListener {
                //callback.onError("Media Author Error: " + it.message + " ${it.networkResponse.statusCode}  ${it.networkResponse.data.toString()}")
                postBoObjects.mediaAuthor = User(-1, "Desconocido", "", "", "", AvatarUrls("", "", ""))
                sendPostObjects(postBoObjects, callback)
                //Log.d("ALVARO", "Media Author Error: " + it.message + " ${it.networkResponse.statusCode}  ${it.networkResponse.data.toString()}")
            }
        )
        VolleySingleton.getInstance().addToRequestQueue(stringRequestMediaAuthor)
    }

    private fun sendPostObjects(postBoObjects: PostBoObjects, callback: ObjectsRequest) {
        if (postBoObjects.isPostCompleted()) {
            callback.onCompleted(postBoObjects)
        }
    }

    private interface ObjectsRequest {
        fun onCompleted(postBoObjects: PostBoObjects)
        fun onError(message :String)
    }

    data class PostBoObjects(
        var post: Post,
        var media: Media? = null,
        var author: User? = null,
        var categories: List<Category>? = null,
        var mediaAuthor: User? = null
    ) {
        fun isPostCompleted() = post != null && media != null && author != null && categories != null && mediaAuthor != null
    }
}