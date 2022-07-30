package com.fiz.battleinthespace.domain.models

data class TypeItems(
    val id: String = "",
    val name: Int = 0,
    val imageId: Int = 0,
    var items: List<Item> = listOf()
) :
    java.io.Serializable