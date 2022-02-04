package com.fiz.android.battleinthespace.interfaces.main.space_station

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R
import com.fiz.android.battleinthespace.databinding.CardImageCaptionCaptionBinding
import com.fiz.android.battleinthespace.options.Product
import com.fiz.android.battleinthespace.options.StateProduct


class ImagesCaptionCaptionAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ImagesCaptionCaptionAdapter.ViewHolder>() {

    fun interface Listener {
        fun onClick(position: Int)
    }

    private lateinit var listener: Listener

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardImageCaptionCaptionBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0)
            holder.bind()
        else
            holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ViewHolder(val binding: CardImageCaptionCaptionBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private val colorDefault = binding.cardView.cardBackgroundColor

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(products: Product) {
            binding.item = products
            val drawable = ContextCompat.getDrawable(itemView.context, products.imageId)

            binding.infoImage.setImageDrawable(drawable)
            binding.infoImage.contentDescription = (products.name + products.cost).toString()
            itemView.isEnabled = products.state != StateProduct.INSTALL
            when (products.state) {
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

            val names = binding.root.context.resources.getString(products.name)
            when (products.state) {
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

            binding.costText.text = binding.root.context.resources.getString(R.string.cost, products.cost)
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
            if (binding.item?.state == StateProduct.NONE && binding.item?.cost != 0) {
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
                    bind(binding.item!!)
                }
                return
            }
            if (binding.costText.visibility == View.GONE) {
                binding.buttonsLayout.visibility = View.GONE
                binding.costText.visibility = View.VISIBLE
                bind(binding.item!!)
                return
            }
            listener.onClick(layoutPosition)
        }

    }
}