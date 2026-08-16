package com.example.sidebarnav.data

import android.content.Context

class Session private constructor(ctx: Context) {
    private val prefs = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

    var userId: Int
        get() = prefs.getInt("user_id", -1)
        set(v) = prefs.edit().putInt("user_id", v).apply()

    var username: String
        get() = prefs.getString("username", "") ?: ""
        set(v) = prefs.edit().putString("username", v).apply()

    val isLoggedIn: Boolean get() = userId != -1

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        @Volatile private var instance: Session? = null
        fun init(ctx: Context) {
            if (instance == null) {
                synchronized(this) {
                    instance = Session(ctx.applicationContext)
                }
            }
        }
        fun get(): Session = instance!!
    }
}
