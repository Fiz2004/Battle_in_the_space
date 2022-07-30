package com.fiz.battleinthespace.domain.models

data class Item(
    val id: String="",
    val name: Int=0,
    val imageId: Int=0,
    val cost: Int=0,
    var state: StateProduct =StateProduct.NONE
) : java.io.Serializable {
    companion object {
        fun addZeroFirstItem(items: List<Item>): List<Item> {
            val result = mutableListOf<Item>()
            result.add(
                Item(
                    "0",
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