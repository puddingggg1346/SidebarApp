package com.example.sidebarnav.net

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val JSON = "application/json; charset=utf-8".toMediaType()
    private const val IPV6_ANY = "http://[::1]:5000" // placeholder

    fun post(path: String, body: JSONObject): JSONObject {
        val req = Request.Builder()
            .url("${ApiConfig.BASE_URL}$path")
            .post(body.toString().toRequestBody(JSON))
            .build()
        client.newCall(req).execute().use { resp ->
            val text = resp.body?.string() ?: "{}"
            return JSONObject(text)
        }
    }

    fun get(path: String): JSONObject {
        val req = Request.Builder()
            .url("${ApiConfig.BASE_URL}$path")
            .get()
            .build()
        client.newCall(req).execute().use { resp ->
            val text = resp.body?.string() ?: "{}"
            return JSONObject(text)
        }
    }

    fun put(path: String, body: JSONObject): JSONObject {
        val req = Request.Builder()
            .url("${ApiConfig.BASE_URL}$path")
            .put(body.toString().toRequestBody(JSON))
            .build()
        client.newCall(req).execute().use { resp ->
            return JSONObject(resp.body?.string() ?: "{}")
        }
    }

    fun delete(path: String, body: JSONObject): JSONObject {
        val req = Request.Builder()
            .url("${ApiConfig.BASE_URL}$path")
            .delete(body.toString().toRequestBody(JSON))
            .build()
        client.newCall(req).execute().use { resp ->
            return JSONObject(resp.body?.string() ?: "{}")
        }
    }
}
