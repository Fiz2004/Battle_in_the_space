package com.fiz.android.battleinthespace.base.presentation.helpers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.base.data.Item
import com.fiz.android.battleinthespace.base.data.StateProduct
import com.fiz.android.battleinthespace.databinding.ListItemItemBinding


class ItemsAdapter(private val Items: List<Item>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    fun interface Listener {
        fun onClick(position: Int)
    }

    private lateinit var listener: Listener

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // position==0 Возникает когда возвращаемся назад из меню покупки.
        if (position == 0)
            holder.bind()
        else
            holder.bind(Items[position])
    }

    override fun getItemCount(): Int {
        return Items.size
    }

    inner class ViewHolder(val binding: ListItemItemBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private val colorDefault = binding.cardView.cardBackgroundColor
        var item: Item? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Item) {
            this.item = item
            val drawable = ContextCompat.getDrawable(itemView.context, item.imageId)

            binding.infoImage.setImageDrawable(drawable)
            binding.infoImage.contentDescription = (item.name + item.cost).toString()
            itemView.isEnabled = item.state != StateProduct.INSTALL
            when (item.state) {
                StateProduct.INSTALL -> {
                    binding.cardView.setCardBackgroundColor(Color.RED)
                }
                StateProduct.BUY -> {
                    binding.cardView.setCardBackgroundColor(Color.GREEN)
                }
                else -> {
                    binding.cardView.setCardBackgroundColor(colorDefault)
                }
            }

            val names = binding.root.context.resources.getString(item.name)
            when (item.state) {
                StateProduct.INSTALL -> {
                    binding.infoText.text =
                        binding.root.context.resources.getString(R.string.install, names)
                }
                StateProduct.BUY -> {
                    binding.infoText.text =
                        binding.root.context.resources.getString(R.string.buying, names)
                }
                else -> {
                    binding.infoText.text = names
                }
            }

            binding.costText.text = binding.root.context.resources.getString(R.string.cost, item.cost)
        }

        fun bind() {
            val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.back)
            binding.infoImage.setImageDrawable(drawable)
            binding.infoImage.contentDescription = "Back"
            itemView.isEnabled = true
            binding.infoText.text = itemView.context.resources.getString(R.string.back)
            binding.costText.text = ""
        }

        override fun onClick(v: View) {
            if (item != null && item?.cost != 0 && item?.state == StateProduct.NONE) {
                binding.cardView.setCardBackgroundColor(Color.YELLOW)
                binding.infoText.text = binding.root.context.resources.getString(R.string.buying_question)
                binding.costText.visibility = View.GONE
                binding.buttonsLayout.visibility = View.VISIBLE
                binding.okButton.setOnClickListener {
                    listener.onClick(layoutPosition)
                }
                binding.undoButton.setOnClickListener {
                    binding.buttonsLayout.visibility = View.GONE
                    binding.costText.visibility = View.VISIBLE
                    bind(item!!)
                }
                return
            }
            if (binding.costText.visibility == View.GONE) {
                binding.buttonsLayout.visibility = View.GONE
                binding.costText.visibility = View.VISIBLE
                bind(item!!)
                return
            }
            listener.onClick(layoutPosition)
        }

    }
}