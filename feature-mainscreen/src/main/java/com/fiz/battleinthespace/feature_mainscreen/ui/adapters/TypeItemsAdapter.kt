package com.fiz.battleinthespace.feature_mainscreen.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.fiz.battleinthespace.domain.models.TypeItems
import com.fiz.battleinthespace.feature_mainscreen.databinding.ListItemTypeItemsBinding

class TypeItemsAdapter(
    val callback: (Int) -> Unit
) : BaseQuickAdapter<TypeItems, TypeItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTypeItemsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: TypeItems?) {
        val item = item ?: return
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ListItemTypeItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemDefault: TypeItems) {
            binding.apply {
                val drawable = ContextCompat.getDrawable(itemView.context, itemDefault.imageId)

                Glide.with(root.context)
                    .load(drawable)
                    .into(infoImage)

                infoImage.contentDescription =
                    itemView.context.resources.getString(itemDefault.name)
                infoText.text = itemView.context.resources.getString(itemDefault.name)

                cardView.setOnClickListener { callback(layoutPosition) }
            }
        }
    }
}