package com.fiz.android.battleinthespace.base.presentation.helpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.base.data.TypeItems
import com.fiz.android.battleinthespace.databinding.ListItemTypeItemsBinding

class TypeItemsAdapter(
    private val itemDefaults: List<TypeItems>,
    val callback: CallBackTypeItemClick) :
    RecyclerView.Adapter<TypeItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTypeItemsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemDefaults[position])
        holder.binding.cardView.setOnClickListener { callback.onClick(position) }
    }

    override fun getItemCount(): Int {
        return itemDefaults.size
    }

    inner class ViewHolder(val binding: ListItemTypeItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemDefault: TypeItems) {
            val drawable = ContextCompat.getDrawable(itemView.context, itemDefault.imageId)
            binding.infoImage.setImageDrawable(drawable)
            binding.infoImage.contentDescription = itemView.context.resources.getString(itemDefault.name)
            binding.infoText.text = itemView.context.resources.getString(itemDefault.name)

            binding.cardView.setOnClickListener { callback.onClick(layoutPosition) }
        }
    }
}

class CallBackTypeItemClick(val typeItem: (Int) -> Unit) {
    fun onClick(position: Int) = typeItem(position)
}
