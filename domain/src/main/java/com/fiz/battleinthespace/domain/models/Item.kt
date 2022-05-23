package com.fiz.battleinthespace.domain.models

data class Item(
    val id: Int,
    val name: Int,
    val imageId: Int,
    val cost: Int,
    var state: StateProduct
) {
    companion object {
        fun addZeroFirstItem(items: List<Item>): List<Item> {
            val result = mutableListOf<Item>()
            result.add(
                Item(
                    0,
                    0,
                    0,
                    0,
                    StateProduct.NONE
                )
            )
            result.addAll(items)
            return result
        }
    }
}