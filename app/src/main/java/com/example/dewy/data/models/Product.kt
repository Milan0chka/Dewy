package com.example.dewy.data.models

data class Product(
    val name: String = "",
    val activeIngredients: List<String> = listOf(),
    val frequency: Int = 1
)
