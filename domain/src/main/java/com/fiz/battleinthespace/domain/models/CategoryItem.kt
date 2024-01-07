package com.fiz.battleinthespace.domain.models

data class CategoryItem(
    val id: String = "",
    val name: Int = 0,
    val imageId: Int = 0,
    var subItems: List<SubItem.Item> = listOf()
) : java.io.Serializable