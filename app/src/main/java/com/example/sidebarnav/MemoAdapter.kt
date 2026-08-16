package com.example.sidebarnav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MemoAdapter(
    private val items: List<Memo>,
    private val onCopy: (Memo) -> Unit,
    private val onEdit: (Memo) -> Unit,
    private val onDelete: (Memo) -> Unit
) : RecyclerView.Adapter<MemoAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card = view.findViewById<MaterialCardView>(R.id.card)
        val tvContent = view.findViewById<TextView>(R.id.tvMemoContent)
        val btnCopy = view.findViewById<MaterialButton>(R.id.btnCopy)
        val btnEdit = view.findViewById<MaterialButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)
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
        holder.btnCopy.setOnClickListener { onCopy(memo) }
        holder.btnEdit.setOnClickListener { onEdit(memo) }
        holder.btnDelete.setOnClickListener { onDelete(memo) }
    }
}
