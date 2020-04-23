package com.iesvirgendelcarmen.periodicowordpress.model.api

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.iesvirgendelcarmen.periodicowordpress.config.Endpoint
import com.iesvirgendelcarmen.periodicowordpress.model.GsonSingleton
import com.iesvirgendelcarmen.periodicowordpress.model.VolleySingleton
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.User

object UserRepositoryVolley: RepositoryDatasource.User {

    override fun readUserById(id: Int, callback: UserCallback.OneUser) {
        callback.onLoading()
        VolleySingleton.getInstance().requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            "${Endpoint.USERS_URL}$id",
            Response.Listener
            {
                val user = GsonSingleton.getInstance().fromJson<User>(it, User::class.java)
                callback.onResponse(user)
            },
            Response.ErrorListener
            {
                callback.onError("VolleyError: ${it.message}")
            }
        )

        VolleySingleton.getInstance().addToRequestQueue(stringRequest)
    }

}