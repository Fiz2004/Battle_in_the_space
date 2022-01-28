package com.fiz.android.battleinthespace.interfaces

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R


class CaptionImageAdapter(val captions: List<String>, val imageIDs: List<Int>) :
    RecyclerView.Adapter<CaptionImageAdapter.ViewHolder>() {

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
            LayoutInflater.from(parent.context).inflate(R.layout.card_caption_image, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.cardView
        val imageView = view.findViewById<ImageView>(R.id.info_image)
        val drawable = ContextCompat.getDrawable(view.context, imageIDs[position])
        imageView.setImageDrawable(drawable)
        imageView.contentDescription = captions[position]
        val textView = view.findViewById<TextView>(R.id.info_text)
        textView.text = captions[position]
        imageView.setOnClickListener {
            if (listener != null)
                listener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return captions.size
    }
}