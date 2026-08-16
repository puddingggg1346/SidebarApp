package com.example.sidebarnav

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val tvContent = findViewById<TextView>(R.id.tv_content)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> tvContent.text = "🏠 主页\n\n欢迎使用侧边栏示例应用\n\n这是主要内容区域，点击侧边栏菜单可切换内容。"
                R.id.nav_about -> tvContent.text = "ℹ️ 关于\n\n应用名称：侧边栏示例\n版本：1.0\n目标：Android 12 (API 31)\n构建：GitHub Actions"
                R.id.nav_settings -> tvContent.text = "⚙️ 设置\n\n点击下方\"外观\"切换深色/浅色模式。"
                R.id.nav_appearance -> toggleTheme()
            }
            Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawers()
            true
        }

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navView)
        }
    }

    private fun toggleTheme() {
        val current = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val isDark = current == android.content.res.Configuration.UI_MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_NO
            else AppCompatDelegate.MODE_NIGHT_YES
        )
        Toast.makeText(this, if (isDark) "已切换为浅色模式" else "已切换为深色模式", Toast.LENGTH_SHORT).show()
    }
}
