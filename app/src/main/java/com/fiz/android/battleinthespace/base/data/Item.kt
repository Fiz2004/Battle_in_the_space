package com.fiz.android.battleinthespace.base.data

data class Item(val name: Int, val imageId: Int, val cost: Int, var state: StateProduct) {
    companion object {
        fun getListProduct(type: Int, items: MutableMap<Int, StateProduct>): List<Item> {
            val result = mutableListOf<Item>()
            result.add(Item(0, 0, 0, StateProduct.NONE))
            val allItems = ItemDefault.createListItems()
            val itemsType = allItems.filter { it.type == type }
            for (item in itemsType) {
                val state = items[item.name] ?: StateProduct.NONE
                result.add(Item(item.name, item.imageId, item.cost, state))
            }
            return result
        }
    }
}