package com.fiz.battleinthespace.feature_mainscreen.ui.space_station

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.feature_mainscreen.R
import com.fiz.battleinthespace.feature_mainscreen.databinding.ListItemItemBinding
import com.fiz.battleinthespace.feature_mainscreen.databinding.ListItemTypeItemsBinding
import com.google.android.material.color.MaterialColors

internal class ItemUiAdapter(
    val onClickListener: (SpaceStationItemUi) -> Unit,
    val onUndoListener: (SpaceStationItemUi) -> Unit,
) : BaseMultiItemAdapter<SpaceStationItemUi>() {

    class CategoryVH(val viewBinding: ListItemTypeItemsBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    class ItemVH(val viewBinding: ListItemItemBinding) : RecyclerView.ViewHolder(viewBinding.root)

    init {
        addItemType(CATEGORY_TYPE, object : OnMultiItemAdapterListener<SpaceStationItemUi, CategoryVH> {
            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): CategoryVH {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemTypeItemsBinding.inflate(inflater, parent, false)
                return CategoryVH(binding)
            }

            override fun onBind(holder: CategoryVH, position: Int, item: SpaceStationItemUi?) {
                require(item is SpaceStationItemUi.CategorySpaceStationItem)

                with(holder) {
                    viewBinding.apply {
                        val drawable = ContextCompat.getDrawable(itemView.context, item.imageId)

                        Glide.with(root.context)
                            .load(drawable)
                            .into(infoImage)

                        infoImage.contentDescription =
                            itemView.context.resources.getString(item.nameId)
                        infoText.text = itemView.context.resources.getString(item.nameId)

                        cardView.setOnClickListener { onClickListener(item) }
                    }
                }
            }
        }).addItemType(ITEM_TYPE, object : OnMultiItemAdapterListener<SpaceStationItemUi, ItemVH> {
            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): ItemVH {
                val inflater = LayoutInflater.from(context)
                val binding = ListItemItemBinding.inflate(inflater, parent, false)
                return ItemVH(binding)
            }

            override fun onBind(holder: ItemVH, position: Int, item: SpaceStationItemUi?) {
                require(item is SpaceStationItemUi.SubSpaceStationItemUi.SubSpaceStationItem)

                with(holder) {
                    viewBinding.apply {
                        val drawable = ContextCompat.getDrawable(itemView.context, item.imageId)
                        imgInfo.setImageDrawable(drawable)
                        imgInfo.contentDescription = (item.nameId + item.cost).toString()
                        itemView.isEnabled = item.state != StateProduct.INSTALL
                        val color: Int = when (item.state) {
                            StateProduct.INSTALL -> {
                                MaterialColors.getColor(root, R.attr.colorTertiary)
                            }

                            StateProduct.BUY -> {
                                MaterialColors.getColor(root, R.attr.colorSecondary)
                            }

                            StateProduct.PREPARE -> {
                                Color.YELLOW
                            }

                            else -> {
                                Color.WHITE
                            }
                        }
                        val colorFont: Int = when (item.state) {
                            StateProduct.INSTALL -> {
                                MaterialColors.getColor(root, R.attr.colorOnTertiary)
                            }

                            StateProduct.BUY -> {
                                MaterialColors.getColor(root, R.attr.colorOnSecondary)
                            }

                            else -> {
                                txtCost.textColors.defaultColor
                            }
                        }
                        cardView.setCardBackgroundColor(color)
                        txtCost.setTextColor(colorFont)
                        txtInfo.setTextColor(colorFont)

                        val names = root.context.resources.getString(item.nameId)
                        val info = when (item.state) {
                            StateProduct.INSTALL ->
                                root.context.resources.getString(R.string.install, names)

                            StateProduct.BUY ->
                                root.context.resources.getString(R.string.buying, names)

                            StateProduct.PREPARE ->
                                root.context.resources.getString(R.string.buying_question)

                            else -> names
                        }
                        txtInfo.text = info
                        txtCost.text =
                            root.context.resources.getString(R.string.cost, item.cost)
                        containerConfirmButtons.isVisible = item.state == StateProduct.PREPARE
                        txtCost.isVisible = item.state != StateProduct.PREPARE
                        btnOk.setOnClickListener {
                            onClickListener(item)
                        }
                        btnUndo.setOnClickListener {
                            onUndoListener(item)
                        }
                        cardView.setOnClickListener {
                            onClickListener(item)
                        }
                    }
                }
            }
        }).addItemType(BACK_TYPE, object : OnMultiItemAdapterListener<SpaceStationItemUi, ItemVH> {
            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): ItemVH {
                val inflater = LayoutInflater.from(context)
                val binding = ListItemItemBinding.inflate(inflater, parent, false)
                return ItemVH(binding)
            }

            override fun onBind(holder: ItemVH, position: Int, item: SpaceStationItemUi?) {
                require(item is SpaceStationItemUi.SubSpaceStationItemUi.BackSpaceStationItem)

                with(holder) {
                    viewBinding.apply {
                        val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.back)
                        imgInfo.setImageDrawable(drawable)
                        imgInfo.contentDescription = "Back"
                        itemView.isEnabled = true
                        txtInfo.text = itemView.context.resources.getString(R.string.back)
                        txtCost.text = ""

                        cardView.setOnClickListener {
                            onClickListener(item)
                        }
                    }
                }
            }
        }).onItemViewType { position, list ->
            when (list[position]) {
                is SpaceStationItemUi.CategorySpaceStationItem -> CATEGORY_TYPE
                is SpaceStationItemUi.SubSpaceStationItemUi.BackSpaceStationItem -> BACK_TYPE
                is SpaceStationItemUi.SubSpaceStationItemUi.SubSpaceStationItem -> ITEM_TYPE
            }
        }
    }

    companion object {
        private const val CATEGORY_TYPE = 0
        private const val ITEM_TYPE = 1
        private const val BACK_TYPE = 2
    }
}