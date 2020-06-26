package com.android.ganhuo.http

data class BaseResponse<T>(
    val data: List<T>? = null,
    val page: Int = 1,
    val page_count: Int = 10,
    val status: Int = 100,
    val total_counts: Int = 100
)