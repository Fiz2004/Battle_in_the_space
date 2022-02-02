package com.fiz.android.battleinthespace.interfaces.main.space_station

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fiz.android.battleinthespace.R

enum class stateProduct {
    NONE, BUY, INSTALL,
}

class ImagesCaptionCaptionAdapter(
    val captions: List<String>,
    val costs: List<Int>,
    val imageIDs: List<Int>,
    val states: List<stateProduct>
) :
    RecyclerView.Adapter<ImagesCaptionCaptionAdapter.ViewHolder>() {

    inner class ViewHolder(val view: CardView) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var caption: String = ""
        private var cost: Int = 0
        private var imageID: Int = 0
        private var state: stateProduct = stateProduct.NONE

        val imageView: ImageView = itemView.findViewById(R.id.info_image)
        val textView: TextView = itemView.findViewById(R.id.info_text)
        val costView: TextView = itemView.findViewById(R.id.cost_text)
        val okButton: TextView = itemView.findViewById(R.id.ok_Button)
        val undoButton: TextView = itemView.findViewById(R.id.undo_Button)
        val buttonsLayout: LinearLayout = itemView.findViewById(R.id.buttons_Layout)
        val colorDefault = view.cardBackgroundColor

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(caption: String, cost: Int, imageID: Int, state: stateProduct) {
            this.caption = caption
            this.cost = cost
            this.imageID = imageID
            this.state = state
            val drawable = ContextCompat.getDrawable(itemView.context, imageID)

            imageView.setImageDrawable(drawable)
            imageView.contentDescription = caption + cost
            itemView.isEnabled = state != stateProduct.INSTALL
            when (state) {
                stateProduct.INSTALL -> {
                    view.setCardBackgroundColor(Color.RED)
                }
                stateProduct.BUY -> {
                    view.setCardBackgroundColor(Color.GREEN)
                }
                else -> {
                    view.setCardBackgroundColor(colorDefault)
                }
            }

            when (state) {
                stateProduct.INSTALL -> {
                    textView.text = caption + view.resources.getString(R.string.install)
                }
                stateProduct.BUY -> {
                    textView.text = caption + view.resources.getString(R.string.buying)
                }
                else -> {
                    textView.text = caption
                }
            }

            costView.text = if (cost == 0)
                ""
            else
                view.resources.getString(R.string.cost) + cost.toString() + "$"
        }

        override fun onClick(v: View) {
            if (state == stateProduct.NONE && cost != 0) {
                view.setCardBackgroundColor(Color.YELLOW)
                textView.text = view.resources.getString(R.string.buying_question)
                costView.visibility = View.GONE
                buttonsLayout.visibility = View.VISIBLE
                okButton.setOnClickListener {
                    listener.onClick(layoutPosition)
                }
                undoButton.setOnClickListener {
                    buttonsLayout.visibility = View.GONE
                    costView.visibility = View.VISIBLE
                    bind(caption, cost, imageID, state)
                }
                return
            }
            if (costView.visibility == View.GONE) {
                buttonsLayout.visibility = View.GONE
                costView.visibility = View.VISIBLE
                bind(caption, cost, imageID, state)
                return
            }
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
            LayoutInflater.from(parent.context).inflate(R.layout.card_image_caption_caption, parent, false) as CardView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(captions[position], costs[position], imageIDs[position], states[position])
    }

    override fun getItemCount(): Int {
        return captions.size
    }
}