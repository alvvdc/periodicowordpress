package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.GsonSingleton
import com.iesvirgendelcarmen.periodicowordpress.model.VolleySingleton
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category

object CategoryRepositoryVolley: RepositoryDatasource.Category {

    override fun readCategoryById(id: Int, callback: CategoryCallback.OneCategory) {
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            Endpoint.CATEGORY_URL + id,
            Response.Listener
            {
                val category = GsonSingleton.getInstance().fromJson<Category>(it, Category::class.java)
                callback.onResponse(category)
            },
            Response.ErrorListener
            {
                callback.onError("VolleyError: ${it.message}")
            }
        )

        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }
}