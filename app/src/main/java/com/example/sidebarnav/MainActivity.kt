package com.example.sidebarnav

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "主页", Toast.LENGTH_SHORT).show()
                    // 内容展示
                    findViewById<android.widget.TextView>(R.id.tv_content).text =
                        "欢迎来到主页\n这是应用的主要内容区域"
                }
                R.id.nav_about -> {
                    Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show()
                    findViewById<android.widget.TextView>(R.id.tv_content).text =
                        "侧边栏示例应用\n版本 1.0\n由 Termux 构建"
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // 点击三按钮时给侧边栏通知
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navView)
        }
    }
}
