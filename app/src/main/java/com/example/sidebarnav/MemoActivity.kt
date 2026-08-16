package com.example.sidebarnav

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sidebarnav.data.Session
import com.example.sidebarnav.ui.theme.SidebarAppTheme
import kotlinx.coroutines.*
import org.json.JSONObject

data class Memo(val id: Int, val content: String)

class MemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SidebarAppTheme {
                MemoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var memos by remember { mutableStateOf(listOf<Memo>()) }
    var loading by remember { mutableStateOf(true) }
    var showEdit by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Memo?>(null) }

    fun refresh() {
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val resp = com.example.sidebarnav.net.ApiClient.get("/memos?user_id=${Session.get().userId}")
                    if (resp.getBoolean("ok")) {
                        val arr = resp.getJSONArray("memos")
                        (0 until arr.length()).map { i ->
                            val o = arr.getJSONObject(i)
                            Memo(o.getInt("id"), o.getString("content"))
                        }
                    } else emptyList()
                } catch (e: Exception) { emptyList() }
            }
            memos = result
            loading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("备忘录") },
                navigationIcon = {
                    IconButton(onClick = { (context as? MemoActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showEdit = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { padding ->
        when {
            loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            memos.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("暂无备忘\n点击右下角 + 添加", textAlign = TextAlign.Center)
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(memos, key = { it.id }) { memo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                memo.content,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            IconButton(onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("memo", memo.content))
                                Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "复制")
                            }
                            IconButton(onClick = { editing = memo; showEdit = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "编辑")
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    val ok = withContext(Dispatchers.IO) {
                                        try {
                                            val body = JSONObject().put("user_id", Session.get().userId)
                                            com.example.sidebarnav.net.ApiClient.delete("/memo/${memo.id}", body).getBoolean("ok")
                                        } catch (e: Exception) { false }
                                    }
                                    if (ok) refresh()
                                    else Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "删除")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEdit) {
        MemoEditDialog(
            initialText = editing?.content ?: "",
            isEdit = editing != null,
            onDismiss = { showEdit = false },
            onSave = { text ->
                showEdit = false
                scope.launch {
                    val ok = withContext(Dispatchers.IO) {
                        try {
                            val body = JSONObject().put("user_id", Session.get().userId).put("content", text)
                            if (editing != null) {
                                com.example.sidebarnav.net.ApiClient.put("/memo/${editing!!.id}", body).getBoolean("ok")
                            } else {
                                com.example.sidebarnav.net.ApiClient.post("/memo", body).getBoolean("ok")
                            }
                        } catch (e: Exception) { false }
                    }
                    refresh()
                    if (!ok) Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun MemoEditDialog(
    initialText: String,
    isEdit: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "编辑备忘" else "新建备忘") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("输入备忘内容") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onSave(text.trim()) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
