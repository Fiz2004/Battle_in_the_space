package com.fiz.battleinthespace.feature_mainscreen.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.fiz.battleinthespace.domain.models.Item
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.ListItemItemBinding
import com.google.android.material.color.MaterialColors


class ItemsAdapter(
    val callback: (Int) -> Unit
) : BaseQuickAdapter<Item, ItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemsAdapter.ViewHolder, position: Int, item: Item?) {
        // position==0 Возникает когда возвращаемся назад из меню покупки.
        if (position == 0) {
            holder.bind()
        } else {
            val item = item ?: return
            holder.bind(item)
        }
    }

    inner class ViewHolder(private val binding: ListItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val colorDefault = binding.cardView.cardBackgroundColor.defaultColor
        private val colorFontDefault = binding.costText.textColors.defaultColor
        var item: Item? = null

        fun bind(item: Item) {
            this.item = item
            val drawable = ContextCompat.getDrawable(itemView.context, item.imageId)

            binding.infoImage.setImageDrawable(drawable)
            binding.infoImage.contentDescription = (item.name + item.cost).toString()
            itemView.isEnabled = item.state != StateProduct.INSTALL
            val color: Int = when (item.state) {
                StateProduct.INSTALL -> {
                    MaterialColors.getColor(binding.root, R.attr.colorTertiary)
                }

                StateProduct.BUY -> {
                    MaterialColors.getColor(binding.root, R.attr.colorSecondary)
                }

                else -> {
                    colorDefault
                }
            }
            val colorFont: Int = when (item.state) {
                StateProduct.INSTALL -> {
                    MaterialColors.getColor(binding.root, R.attr.colorOnTertiary)
                }

                StateProduct.BUY -> {
                    MaterialColors.getColor(binding.root, R.attr.colorOnSecondary)
                }

                else -> {
                    colorFontDefault
                }
            }
            binding.cardView.setCardBackgroundColor(color)
            binding.costText.setTextColor(colorFont)
            binding.infoText.setTextColor(colorFont)

            val names = binding.root.context.resources.getString(item.name)
            val info = when (item.state) {
                StateProduct.INSTALL ->
                    binding.root.context.resources.getString(R.string.install, names)

                StateProduct.BUY ->
                    binding.root.context.resources.getString(R.string.buying, names)

                else -> names
            }
            binding.infoText.text = info
            binding.costText.text =
                binding.root.context.resources.getString(R.string.cost, item.cost)

            binding.cardView.setOnClickListener {
                if (item.cost != 0 && item.state == StateProduct.NONE) {
                    binding.cardView.setCardBackgroundColor(Color.YELLOW)
                    binding.infoText.text =
                        binding.root.context.resources.getString(R.string.buying_question)
                    binding.costText.visibility = View.GONE
                    binding.buttonsLayout.visibility = View.VISIBLE
                    binding.okButton.setOnClickListener {
                        callback(layoutPosition)
                    }
                    binding.undoButton.setOnClickListener {
                        binding.buttonsLayout.visibility = View.GONE
                        binding.costText.visibility = View.VISIBLE
                        bind(item)
                    }
                    return@setOnClickListener
                }
                callback(layoutPosition)
            }
        }

        fun bind() {
            val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.back)
            binding.infoImage.setImageDrawable(drawable)
            binding.infoImage.contentDescription = "Back"
            itemView.isEnabled = true
            binding.infoText.text = itemView.context.resources.getString(R.string.back)
            binding.costText.text = ""

            binding.cardView.setOnClickListener {
                callback(layoutPosition)
            }
        }
    }
}