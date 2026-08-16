package com.example.sidebarnav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class MemoAdapter(
    private val items: List<Memo>,
    private val onEdit: (Memo) -> Unit,
    private val onDelete: (Memo) -> Unit
) : RecyclerView.Adapter<MemoAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card = view.findViewById<MaterialCardView>(R.id.card)
        val tvContent = view.findViewById<TextView>(R.id.tvMemoContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val memo = items[position]
        holder.tvContent.text = memo.content
        holder.card.setOnLongClickListener {
            val popup = PopupMenu(holder.card.context, holder.card)
            popup.menu.add("编辑")
            popup.menu.add("删除")
            popup.setOnMenuItemClickListener { item ->
                when (item.title.toString()) {
                    "编辑" -> onEdit(memo)
                    "删除" -> onDelete(memo)
                }
                true
            }
            popup.show()
            true
        }
    }
}
