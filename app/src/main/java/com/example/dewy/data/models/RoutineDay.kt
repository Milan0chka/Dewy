package com.example.dewy.data.models

data class RoutineDay(
    val dayName: String = "",
    var steps: List<RoutineStep> = mutableListOf()
)
