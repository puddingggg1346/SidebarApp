package com.example.sidebarnav.ui

import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.sidebarnav.MemoActivity
import com.example.sidebarnav.data.Session

@Composable
fun AppRoot() {
    val context = LocalContext.current
    var screen by remember { mutableStateOf("home") }
    var loggedIn by remember { mutableStateOf(Session.get().isLoggedIn) }

    when (screen) {
        "home" -> HomeScreen(
            loggedIn = loggedIn,
            onShowLogin = { screen = "login" },
            onShowMemo = {
                if (loggedIn) context.startActivity(Intent(context, MemoActivity::class.java))
                else screen = "login"
            }
        )
        "login" -> LoginScreen(
            onBack = { screen = "home" },
            onLoggedIn = {
                loggedIn = true
                screen = "home"
            }
        )
    }
}
