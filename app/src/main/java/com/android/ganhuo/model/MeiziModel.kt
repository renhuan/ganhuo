package com.example.myapplication.model

import com.blankj.utilcode.util.TimeUtils

data class MeiziModel(
    val _id: String,
    val author: String,
    val category: String,
    val createdAt: String,
    val desc: String,
    var images: List<String>,
    val likeCounts: Int,
    val publishedAt: String,
    val stars: Int,
    val title: String,
    val type: String,
    val url: String,
    val views: Int
) {
    fun getPublishedAt_() = TimeUtils.getFriendlyTimeSpanByNow(publishedAt)
    fun getImage_() = if (images.isEmpty()) "" else images[0]
}