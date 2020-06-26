package com.example.myapplication.model

import com.blankj.utilcode.util.TimeUtils

data class Data(
    val _id: String,
    val author: String,
    val category: String,
    val createdAt: String,
    val desc: String,
    val images: List<String>,
    val likeCounts: Int,
    val publishedAt: String,
    val stars: Int,
    val title: String,
    val type: String,
    val url: String,
    val views: Int
) {
    fun getmPublishedAt() = TimeUtils.getFriendlyTimeSpanByNow(publishedAt)
    fun getmImage() = if (images.isEmpty()) "" else images[0]
}