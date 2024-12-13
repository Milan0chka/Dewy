package com.example.dewy.data.models

data class Record(
    val date: String = "",
    val image_url: String = "",
    val comment: String? = null,
    val tags: List<String> = emptyList()
)