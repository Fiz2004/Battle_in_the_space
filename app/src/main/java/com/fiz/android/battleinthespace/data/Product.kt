package com.fiz.android.battleinthespace.data

data class Product(val name: Int, val imageId: Int, val cost: Int, var state: StateProduct) {
    companion object {
        fun getListProduct(type: Int, items: MutableMap<Int, StateProduct>): List<Product> {
            val result = mutableListOf<Product>()
            result.add(Product(0, 0, 0, StateProduct.NONE))
            val allItems = Products.createListProducts()
            val itemsType = allItems.filter { it.type == type }
            for (item in itemsType) {
                val state = items[item.name] ?: StateProduct.NONE
                result.add(Product(item.name, item.imageId, item.cost, state))
            }
            return result
        }
    }
}