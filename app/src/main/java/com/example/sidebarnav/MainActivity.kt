package com.example.sidebarnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.sidebarnav.data.Session
import com.example.sidebarnav.ui.AppRoot
import com.example.sidebarnav.ui.theme.SidebarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Session.init(this)
        setContent {
            SidebarAppTheme {
                AppRoot()
            }
        }
    }
}
