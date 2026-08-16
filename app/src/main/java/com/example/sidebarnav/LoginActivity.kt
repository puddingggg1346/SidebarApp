package com.example.sidebarnav

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.sidebarnav.data.Session
import com.example.sidebarnav.databinding.ActivityLoginBinding
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnLogin.setOnClickListener {
            val u = binding.etUsername.text.toString().trim()
            val p = binding.etPassword.text.toString()
            doAuth(u, p, "login")
        }

        binding.btnRegister.setOnClickListener {
            val u = binding.etUsername.text.toString().trim()
            val p = binding.etPassword.text.toString()
            doAuth(u, p, "register")
        }
    }

    private fun doAuth(username: String, password: String, mode: String) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show(); return
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
                        Triple(true, "")
                    } else Triple(false, resp.getString("msg"))
                } catch (e: Exception) {
                    Triple(false, "无法连接服务器: ${e.message}")
                }
            }
            binding.btnLogin.isEnabled = true
            val (ok, msg) = result
            if (ok) {
                Toast.makeText(this, "欢迎，${Session.get().username}", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                AlertDialog.Builder(this).setTitle("提示").setMessage(msg).setPositiveButton("好", null).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed(); return true
    }
}
