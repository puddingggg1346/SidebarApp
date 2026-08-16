package com.example.sidebarnav

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import com.example.sidebarnav.data.Session
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Session.init(this)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val tvContent = findViewById<TextView>(R.id.tv_content)
        val navAccount = navView.menu.findItem(R.id.nav_account)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fun updateAccountTitle() {
            navAccount.title = if (Session.get().isLoggedIn)
                "${Session.get().username}（退出）" else "账号（登录/注册）"
        }
        updateAccountTitle()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> tvContent.text = "🏠 主页\n\n欢迎使用侧边栏应用\nMaterial You 动态取色"
                R.id.nav_memo -> {
                    if (Session.get().isLoggedIn) {
                        startActivity(Intent(this, MemoActivity::class.java))
                    } else {
                        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
                R.id.nav_about -> tvContent.text = "ℹ️ 关于\n\n侧边栏示例\nMaterial You · API 31\n服务端: Python Flask"
                R.id.nav_appearance -> toggleTheme()
                R.id.nav_account -> {
                    if (Session.get().isLoggedIn) {
                        Session.get().logout()
                        updateAccountTitle()
                        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show()
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navView)
        }
    }

    override fun onResume() {
        super.onResume()
        val navAccount = findViewById<NavigationView>(R.id.nav_view).menu.findItem(R.id.nav_account)
        navAccount.title = if (Session.get().isLoggedIn)
            "${Session.get().username}（退出）" else "账号（登录/注册）"
    }

    private fun toggleTheme() {
        val current = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val isDark = current == android.content.res.Configuration.UI_MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_NO
            else AppCompatDelegate.MODE_NIGHT_YES
        )
        Toast.makeText(this, if (isDark) "已切换浅色" else "已切换深色", Toast.LENGTH_SHORT).show()
    }
}
