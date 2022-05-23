package com.fiz.battleinthespace.feature_mainscreen.ui.statistics

import android.view.LayoutInflater
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fiz.battleinthespace.database.ItemsDatabase
import com.fiz.battleinthespace.domain.models.Player
import com.fiz.battleinthespace.feature_mainscreen.databinding.ListItemStatisticBinding

const val ITEM_HEADER = 0
const val ITEM_PLAYER = 1

class StatisticsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data: List<Player> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemStatisticBinding.inflate(inflater, parent, false)
        when (viewType) {
            ITEM_HEADER -> return HeaderViewHolder(binding)
        }
        return StatisticViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> (holder as HeaderViewHolder).bind("Полная статистика по игрокам")
            1 -> (holder as HeaderViewHolder).bind("Игроки")
            2 -> (holder as StatisticViewHolder).bind(data[position])
            3 -> (holder as HeaderViewHolder).bind("Компьютер")
            else -> (holder as StatisticViewHolder).bind(data[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_HEADER
            1 -> ITEM_HEADER
            2 -> ITEM_PLAYER
            3 -> ITEM_HEADER
            else -> ITEM_PLAYER
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(players: List<Player>) {
        data = listOf(Player(items = ItemsDatabase.getStartItems())) + listOf(
            Player(items = ItemsDatabase.getStartItems())
        ) + listOf(players[0]) + listOf(
            Player(items = ItemsDatabase.getStartItems())
        ) + listOf(
            players[1],
            players[2],
            players[3]
        )
        notifyDataSetChanged()
    }

    class StatisticViewHolder(val binding: ListItemStatisticBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {
            binding.statisticTextView.text = "${player.name}: ${player.money}"
        }
    }

    class HeaderViewHolder(val binding: ListItemStatisticBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.statisticTextView.textAlignment = TEXT_ALIGNMENT_CENTER
            binding.statisticTextView.text = text
        }
    }
}