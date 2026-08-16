package com.example.sidebarnav.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sidebarnav.MemoActivity
import com.example.sidebarnav.data.Session

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val MEMO = "memo"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var loggedIn by remember { mutableStateOf(Session.get().isLoggedIn) }

    NavHost(navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                loggedIn = Session.get().isLoggedIn,
                onShowLogin = { navController.navigate(Routes.LOGIN) },
                onShowMemo = {
                    if (Session.get().isLoggedIn) {
                        context.startActivity(Intent(context, MemoActivity::class.java))
                    } else {
                        navController.navigate(Routes.LOGIN)
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoggedIn = {
                    navController.popBackStack()
                }
            )
        }
    }
}
