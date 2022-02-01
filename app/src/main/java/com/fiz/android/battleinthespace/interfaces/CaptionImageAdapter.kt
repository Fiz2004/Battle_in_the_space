package com.fiz.android.battleinthespace.interfaces

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R

class CaptionImageAdapter(val captions: List<String>, val imageIDs: List<Int>) :
    RecyclerView.Adapter<CaptionImageAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var caption: String = ""
        private var imageID: Int = 0

        val imageView: ImageView = itemView.findViewById(R.id.info_image)
        val textView: TextView = itemView.findViewById(R.id.info_text)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(caption: String, imageID: Int) {
            this.caption = caption
            this.imageID = imageID
            val drawable = ContextCompat.getDrawable(itemView.context, imageID)
            imageView.setImageDrawable(drawable)
            imageView.contentDescription = caption
            textView.text = caption
        }

        override fun onClick(p0: View?) {
            listener.onClick(layoutPosition)
        }
    }

    fun interface Listener {
        fun onClick(position: Int)
    }

    private lateinit var listener: Listener

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_caption_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(captions[position], imageIDs[position])
    }

    override fun getItemCount(): Int {
        return captions.size
    }
}