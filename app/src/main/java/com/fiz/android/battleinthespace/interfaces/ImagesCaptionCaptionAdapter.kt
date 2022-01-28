package com.fiz.android.battleinthespace.interfaces

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R

enum class stateProduct {
    NONE, BUY, INSTALL
}

class ImagesCaptionCaptionAdapter(
    val captions: List<String>,
    val cost: List<Int>,
    val imageIDs: List<Int>,
    val states: List<stateProduct>) :
    RecyclerView.Adapter<ImagesCaptionCaptionAdapter.ViewHolder>() {

    fun interface Listener {
        fun onClick(position: Int)
    }

    private lateinit var listener: Listener

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_image_caption_caption, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.cardView
        val imageView = view.findViewById<ImageView>(R.id.info_image)
        val drawable = ContextCompat.getDrawable(view.context, imageIDs[position])
        imageView.setImageDrawable(drawable)
        imageView.contentDescription = captions[position] + cost[position]
        imageView.isEnabled = !(states[position] == stateProduct.INSTALL)
        if (states[position] == stateProduct.INSTALL)
            view.setCardBackgroundColor(Color.RED)
        if (states[position] == stateProduct.BUY)
            view.setCardBackgroundColor(Color.GREEN)
        val textView = view.findViewById<TextView>(R.id.info_text)
        textView.text = captions[position]
        val costView = view.findViewById<TextView>(R.id.cost_text)
        if (cost[position] != 0)
            costView.text = cost[position].toString() + "$"
        else
            costView.text = ""

        imageView.setOnClickListener {
            if (listener != null)
                listener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return captions.size
    }
}