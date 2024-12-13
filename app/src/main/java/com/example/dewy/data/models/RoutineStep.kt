package com.example.dewy.data.models

data class RoutineStep(
    val stepName: String = "",
    var products: MutableList<Product> = mutableListOf()
)
