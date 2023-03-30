package com.example.myuca_android.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myuca_android.Coordinador
import com.example.myuca_android.CoordinadorResponse
import com.example.myuca_android.LocalDateSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate

class CoordinadorManager(val context: Context) {
    private val gson: Gson

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(
            LocalDate::class.java,
            LocalDateSerializer()
        )
        gson = gsonBuilder.create()
    }

    companion object {
        val BASE_URL = "http://192.168.1.7:8080/~jezerm/MyUCA-api"
    }

    fun getCoordinador(
        id: Int,
        listener: (Coordinador) -> Unit,
        errorListener: (VolleyError) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)
        val url =
            Uri.parse(BASE_URL).buildUpon().appendPath("getCoordinador.php")
                .appendQueryParameter("idC", id.toString())
                .build().toString()

        val request = StringRequest(
            Request.Method.GET, url,
            {
                Log.d("MyUCA", "Response: $it")
                val response = gson.fromJson(it, Coordinador::class.java)

                listener(response)
            },
            {
                errorListener(it)
            }
        )
        queue.add(request)
    }

    fun getCoordinadores(
        listener: (List<Coordinador>) -> Unit,
        errorListener: (VolleyError) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)
        val url = "$BASE_URL/getCoordinador.php"

        val request = StringRequest(
            Request.Method.GET, url,
            {
                Log.d("MyUCA", "Response: $it")
                val response = gson.fromJson(it, CoordinadorResponse::class.java)
                val list = response.data
                Log.d("MyUCA", "Coordinadores: $list")

                listener(list)
            },
            {
                errorListener(it)
            }
        )
        queue.add(request)
    }

    fun deleteCoordinador(
        id: Int,
        listener: () -> Unit,
        errorListener: (VolleyError) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)
        val url =
            Uri.parse(BASE_URL).buildUpon().appendPath("deleteCoordinador.php")
                .appendQueryParameter("idC", id.toString())
                .build().toString()

        val request = StringRequest(
            Request.Method.DELETE, url,
            {
                listener()
            },
            {
                errorListener(it)
                Log.d("MyUCA", "Error", it)
            },
        )
        queue.add(request)
    }

    fun insertCoordinador(
        coordinador: Coordinador,
        listener: () -> Unit,
        errorListener: (VolleyError) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)
        val url =
            Uri.parse(BASE_URL).buildUpon().appendPath("insertCoordinador.php")
                .build().toString()

        val json = gson.toJson(coordinador)

        val request = object : StringRequest(
            Method.POST, url,
            {
                listener()
            }, {
                errorListener(it)
                Log.d("MyUCA", "Error", it)
            }
        ) {
            override fun getBody(): ByteArray {
                return json.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        queue.add(request)
    }

    fun modifyCoordinador(
        coordinador: Coordinador,
        listener: () -> Unit,
        errorListener: (VolleyError) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)
        val url =
            Uri.parse(BASE_URL).buildUpon().appendPath("updateCoordinador.php")
                .build().toString()

        val json = gson.toJson(coordinador)

        val request = object : StringRequest(
            Method.POST, url,
            {
                listener()
            }, {
                errorListener(it)
                Log.d("MyUCA", "Error", it)
            }
        ) {
            override fun getBody(): ByteArray {
                return json.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        queue.add(request)
    }
}