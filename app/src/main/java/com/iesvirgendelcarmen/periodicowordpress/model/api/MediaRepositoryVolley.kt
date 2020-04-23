package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.GsonSingleton
import com.iesvirgendelcarmen.periodicowordpress.model.VolleySingleton
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Media

object MediaRepositoryVolley: RepositoryDatasource.Media {
    override fun readMediaById(id: Int, callback: MediaCallback.OneMedia) {
        callback.onLoading()
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            "${Endpoint.MEDIA_URL}$id",
            Response.Listener
            {
                val media = GsonSingleton.getInstance().fromJson<Media>(it, Media::class.java)
                callback.onResponse(media)
            },
            Response.ErrorListener
            {
                callback.onError("VolleyError: ${it.message}")
            }
        )

        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }
}