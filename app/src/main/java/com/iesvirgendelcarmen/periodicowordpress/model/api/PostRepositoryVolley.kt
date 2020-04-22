package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.GsonSingleton
import com.iesvirgendelcarmen.periodicowordpress.model.VolleySingleton
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Post

object PostRepositoryVolley: RepositoryDatasource.Post {
    override fun readAllPosts(callback: PostCallback.ListPosts) {
        callback.onLoading()
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            Endpoint.POSTS_URL,
            Response.Listener
            {
                val listType = object: TypeToken<List<Post>>() {}.type
                val posts = GsonSingleton.getInstance().fromJson<List<Post>>(it, listType)

                callback.onResponse(posts)
            },
            Response.ErrorListener
            {
                callback.onError("VolleyError: ${it.message}")
            }
        )

        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }
}