package com.example.sidebarnav.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sidebarnav.data.Session
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoggedIn: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("账号登录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack
                            .let { androidx.compose.material3.Icon(it, contentDescription = "返回") }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("用户名") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    loading = true
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            try {
                                val body = org.json.JSONObject()
                                    .put("username", username.trim())
                                    .put("password", password)
                                val resp = com.example.sidebarnav.net.ApiClient.post("/login", body)
                                if (resp.getBoolean("ok")) {
                                    Session.get().userId = resp.getInt("user_id")
                                    Session.get().username = resp.getString("username")
                                    null
                                } else resp.getString("msg")
                            } catch (e: Exception) {
                                "无法连接服务器: ${e.message}"
                            }
                        }
                        loading = false
                        if (result == null) {
                            Toast.makeText(context, "欢迎，${Session.get().username}", Toast.LENGTH_SHORT).show()
                            onLoggedIn()
                        } else {
                            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("登录")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    loading = true
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            try {
                                val body = org.json.JSONObject()
                                    .put("username", username.trim())
                                    .put("password", password)
                                val resp = com.example.sidebarnav.net.ApiClient.post("/register", body)
                                if (resp.getBoolean("ok")) {
                                    // 注册成功，自动登录拿 user_id
                                    val loginBody = org.json.JSONObject()
                                        .put("username", username.trim())
                                        .put("password", password)
                                    val loginResp = com.example.sidebarnav.net.ApiClient.post("/login", loginBody)
                                    if (loginResp.getBoolean("ok")) {
                                        Session.get().userId = loginResp.getInt("user_id")
                                        Session.get().username = loginResp.getString("username")
                                        null
                                    } else "注册成功但自动登录失败，请手动登录"
                                } else resp.optString("msg", "注册失败")
                            } catch (e: Exception) {
                                "无法连接服务器: ${e.message}"
                            }
                        }
                        loading = false
                        if (result == null) {
                            Toast.makeText(context, "注册成功，欢迎 ${Session.get().username}", Toast.LENGTH_SHORT).show()
                            onLoggedIn()
                        } else {
                            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("注册新账号")
            }
        }
    }
}
