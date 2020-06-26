package com.example.myapplication.model

data class MeiziModel(
    val `data`: List<Data>,
    val page: Int,
    val page_count: Int,
    val status: Int,
    val total_counts: Int
)