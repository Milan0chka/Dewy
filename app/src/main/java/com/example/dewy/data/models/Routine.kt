package com.example.dewy.data.models

data class Routine(
    val type: String = "",
    val preferredTime: String = "",
    val days: Array<RoutineDay> = Array(7) { index ->
        RoutineDay(dayName = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")[index])
    }
)
