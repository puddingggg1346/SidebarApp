package com.example.sidebarnav

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sidebarnav.data.Session
import com.example.sidebarnav.databinding.ActivityMemoBinding
import kotlinx.coroutines.*
import org.json.JSONObject

data class Memo(val id: Int, val content: String)

class MemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val memos = mutableListOf<Memo>()
    private lateinit var adapter: MemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!Session.get().isLoggedIn) {
            finish(); return
        }

        adapter = MemoAdapter(memos, ::onEdit, ::onDelete)
        binding.rvMemos.layoutManager = LinearLayoutManager(this)
        binding.rvMemos.adapter = adapter

        binding.fabAdd.setOnClickListener { showEditDialog(null) }
        loadMemos()
    }

    private fun loadMemos() {
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val resp = com.example.sidebarnav.net.ApiClient.get("/memos?user_id=${Session.get().userId}")
                    if (resp.getBoolean("ok")) {
                        val arr = resp.getJSONArray("memos")
                        val list = mutableListOf<Memo>()
                        for (i in 0 until arr.length()) {
                            val o = arr.getJSONObject(i)
                            list.add(Memo(o.getInt("id"), o.getString("content")))
                        }
                        Result.success(list)
                    } else Result.failure(Exception(resp.getString("msg")))
                } catch (e: Exception) { Result.failure(e) }
            }
            result.onSuccess {
                memos.clear(); memos.addAll(it); adapter.notifyDataSetChanged()
            }.onFailure {
                Toast.makeText(this, "加载失败: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditDialog(memo: Memo?) {
        val input = android.widget.EditText(this)
        input.hint = "输入备忘内容"
        input.setText(memo?.content ?: "")
        AlertDialog.Builder(this)
            .setTitle(if (memo == null) "新建备忘" else "编辑备忘")
            .setView(input)
            .setPositiveButton("保存") { _, _ ->
                val content = input.text.toString().trim()
                if (content.isNotEmpty()) {
                    if (memo == null) addMemo(content) else updateMemo(memo.id, content)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun addMemo(content: String) {
        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                try {
                    val body = JSONObject().put("user_id", Session.get().userId).put("content", content)
                    com.example.sidebarnav.net.ApiClient.post("/memo", body).getBoolean("ok")
                } catch (e: Exception) { false }
            }
            if (ok) loadMemos() else Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onEdit(memo: Memo) = showEditDialog(memo)

    private fun onDelete(memo: Memo) {
        AlertDialog.Builder(this)
            .setTitle("删除备忘")
            .setMessage("确定删除这条备忘吗？")
            .setPositiveButton("删除") { _, _ ->
                scope.launch {
                    val ok = withContext(Dispatchers.IO) {
                        try {
                            val body = JSONObject().put("user_id", Session.get().userId)
                            com.example.sidebarnav.net.ApiClient.delete("/memo/${memo.id}", body).getBoolean("ok")
                        } catch (e: Exception) { false }
                    }
                    if (ok) {
                        memos.remove(memo); adapter.notifyDataSetChanged()
                    } else Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun updateMemo(id: Int, content: String) {
        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                try {
                    val body = JSONObject().put("user_id", Session.get().userId).put("content", content)
                    com.example.sidebarnav.net.ApiClient.put("/memo/$id", body).getBoolean("ok")
                } catch (e: Exception) { false }
            }
            if (ok) loadMemos() else Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressed(); return true }
}
