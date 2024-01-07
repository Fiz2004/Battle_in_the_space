package com.fiz.battleinthespace.domain.models

sealed class SubItem {

    data object BackItem : SubItem()

    data class Item(
        val id: String = "",
        val name: Int = 0,
        val imageId: Int = 0,
        val cost: Int = 0,
        var state: StateProduct = StateProduct.NONE
    ) : java.io.Serializable, SubItem()

    companion object {
        fun addZeroFirstItem(subItems: List<SubItem>): List<SubItem> {
            return buildList {
                add(BackItem)
                addAll(subItems)
            }
        }
    }
}