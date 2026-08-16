package com.example.sidebarnav

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sidebarnav.data.Session
import com.example.sidebarnav.databinding.ActivityLoginBinding
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ctx = this

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnLogin.setOnClickListener { doAuth("login") }
        binding.btnRegister.setOnClickListener { doAuth("register") }
    }

    private fun doAuth(mode: String) {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(ctx, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
            return
        }
        binding.btnLogin.isEnabled = false
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val body = org.json.JSONObject()
                        .put("username", username)
                        .put("password", password)
                    val resp = com.example.sidebarnav.net.ApiClient.post("/$mode", body)
                    if (resp.getBoolean("ok")) {
                        Session.get().userId = resp.getInt("user_id")
                        Session.get().username = resp.getString("username")
                        null
                    } else {
                        resp.getString("msg")
                    }
                } catch (e: Exception) {
                    "无法连接服务器: ${e.message}"
                }
            }
            binding.btnLogin.isEnabled = true
            if (result == null) {
                Toast.makeText(ctx, "欢迎，${Session.get().username}", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                AlertDialog.Builder(ctx).setTitle("提示").setMessage(result).setPositiveButton("好", null).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed(); return true
    }
}
