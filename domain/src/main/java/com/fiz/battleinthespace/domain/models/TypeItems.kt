package com.fiz.battleinthespace.domain.models

data class TypeItems(val id: Int, val name: Int, val imageId: Int, var items: MutableList<Item>) :
    java.io.Serializable