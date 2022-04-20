package com.fiz.battleinthespace.feature_mainscreen.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.battleinthespace.database.models.TypeItems
import com.fiz.battleinthespace.feature_mainscreen.databinding.ListItemTypeItemsBinding

class TypeItemsAdapter(
    private val typeItems: List<TypeItems>,
    val callback: (Int) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTypeItemsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(typeItems[position], callback)
        holder.binding.cardView.setOnClickListener { callback(position) }
    }

    override fun getItemCount(): Int {
        return typeItems.size
    }
}

class ViewHolder(val binding: ListItemTypeItemsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(itemDefault: TypeItems, callback: (Int) -> Unit) {
        val drawable = ContextCompat.getDrawable(itemView.context, itemDefault.imageId)
        binding.infoImage.setImageDrawable(drawable)
        binding.infoImage.contentDescription =
            itemView.context.resources.getString(itemDefault.name)
        binding.infoText.text = itemView.context.resources.getString(itemDefault.name)

        binding.cardView.setOnClickListener { callback(layoutPosition) }
    }
}