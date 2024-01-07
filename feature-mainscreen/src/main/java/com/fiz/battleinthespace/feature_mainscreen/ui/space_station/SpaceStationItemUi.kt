package com.fiz.battleinthespace.feature_mainscreen.ui.space_station

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.fiz.battleinthespace.domain.models.CategoryItem
import com.fiz.battleinthespace.domain.models.StateProduct
import com.fiz.battleinthespace.domain.models.SubItem
import com.fiz.battleinthespace.domain.models.SubItem.Item
import com.fiz.battleinthespace.feature_mainscreen.R

internal sealed class SpaceStationItemUi {

    data class CategorySpaceStationItem(
        val id: String,
        @StringRes val nameId: Int,
        @DrawableRes val imageId: Int,
        val subItemsUi: List<SubItem>
    ) : SpaceStationItemUi()

    sealed class SubSpaceStationItemUi : SpaceStationItemUi() {

        data class SubSpaceStationItem(
            val id: String,
            @StringRes val nameId: Int,
            @DrawableRes val imageId: Int,
            val cost: Int,
            val state: StateProduct
        ) : SubSpaceStationItemUi() {

            fun toItem(): Item {
                return Item(
                    id = id,
                    name = nameId,
                    imageId = imageId,
                    cost = cost,
                    state = state,
                )
            }
        }

        data object BackSpaceStationItem : SubSpaceStationItemUi()

        companion object {

            fun addBack(items: List<SubSpaceStationItemUi>): List<SubSpaceStationItemUi> {
                return buildList {
                    add(BackSpaceStationItem)
                    addAll(items)
                }
            }
        }
    }
}

internal fun SubItem.toItemUi(): SpaceStationItemUi.SubSpaceStationItemUi {
    return when (this) {
        SubItem.BackItem -> SpaceStationItemUi.SubSpaceStationItemUi.BackSpaceStationItem
        is Item -> SpaceStationItemUi.SubSpaceStationItemUi.SubSpaceStationItem(
            id = id,
            nameId = if (name == 0) {
                R.string.back
            } else {
                name
            },
            imageId = if (imageId == 0) {
                R.drawable.back
            } else {
                imageId
            },
            cost = cost,
            state = state,
        )
    }
}

internal fun CategoryItem.toItemUi(): SpaceStationItemUi.CategorySpaceStationItem {
    return SpaceStationItemUi.CategorySpaceStationItem(
        id = id,
        nameId = name,
        imageId = imageId,
        subItemsUi = subItems,
    )
}