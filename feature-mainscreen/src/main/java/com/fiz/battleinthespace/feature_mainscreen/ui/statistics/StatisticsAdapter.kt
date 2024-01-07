package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.fiz.battleinthespace.feature_mainscreen.databinding.ListItemStatisticBinding

internal class StatisticsAdapter : BaseMultiItemAdapter<StatisticItemUi>() {

    class HeaderVH(val viewBinding: ListItemStatisticBinding) : RecyclerView.ViewHolder(viewBinding.root)

    class StatisticVH(val viewBinding: ListItemStatisticBinding) : RecyclerView.ViewHolder(viewBinding.root)

    init {
        addItemType(HEADER_TYPE, object : OnMultiItemAdapterListener<StatisticItemUi, HeaderVH> {
            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): HeaderVH {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemStatisticBinding.inflate(inflater, parent, false)
                return HeaderVH(binding)
            }

            override fun onBind(holder: HeaderVH, position: Int, item: StatisticItemUi?) {
                require(item is StatisticItemUi.HeaderItem)

                with(holder) {
                    viewBinding.apply {
                        txtStatistic.textAlignment = TEXT_ALIGNMENT_CENTER
                        txtStatistic.text = item.text
                    }
                }
            }
        }).addItemType(
            STATISTIC_TYPE,
            object : OnMultiItemAdapterListener<StatisticItemUi, StatisticVH> {
                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): StatisticVH {
                    val inflater = LayoutInflater.from(context)
                    val binding = ListItemStatisticBinding.inflate(inflater, parent, false)
                    return StatisticVH(binding)
                }

                override fun onBind(holder: StatisticVH, position: Int, item: StatisticItemUi?) {
                    require(item is StatisticItemUi.StatisticItem)

                    with(holder) {
                        viewBinding.apply {
                            txtStatistic.text = "${item.name}: ${item.money}"
                        }
                    }
                }
            }).onItemViewType { position, list ->
            when (list[position]) {
                is StatisticItemUi.HeaderItem -> HEADER_TYPE
                is StatisticItemUi.StatisticItem -> STATISTIC_TYPE
            }
        }
    }

    companion object {
        private const val HEADER_TYPE = 0
        private const val STATISTIC_TYPE = 1
    }
}