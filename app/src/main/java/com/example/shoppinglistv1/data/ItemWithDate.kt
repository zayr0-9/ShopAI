package com.example.shoppinglistv1.data

data class ItemWithDate(
    val name: String,
    val author: String,
    val quantity: Int,
    val purchased: Boolean,
    val date: Long,
    val price: Double
)
