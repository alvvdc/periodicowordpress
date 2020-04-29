package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.reflect.TypeToken
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.GsonSingleton
import com.iesvirgendelcarmen.periodicowordpress.model.VolleySingleton
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category

object CategoryRepositoryVolley: RepositoryDatasource.Category {

    override fun readCategoryById(id: Int, callback: CategoryCallback.OneCategory) {
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            Endpoint.CATEGORIES_URL + id,
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

    override fun readAllCategories(callback: CategoryCallback.ListCategory, page: Int, perPage:Int) {
        callback.onLoading()
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            Endpoint.CATEGORIES_URL + "${Endpoint.ORDER_BY_COUNT}${Endpoint.ORDER_DESC}${Endpoint.PAGE}$page${Endpoint.PER_PAGE}$perPage",
            Response.Listener {
                val listType = object: TypeToken<List<Category>>() {}.type
                val categories = GsonSingleton.getInstance().fromJson<List<Category>>(it, listType)
                callback.onResponse(categories)
            },
            Response.ErrorListener {
                callback.onError("VolleyError: ${it.message}")
            }
        )

        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }
}