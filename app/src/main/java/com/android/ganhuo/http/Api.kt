package com.android.ganhuo.http

import com.android.ganhuo.model.BannerModel
import com.example.myapplication.model.MeiziModel
import rxhttp.toStr
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseList


/**
 * created by renhuan
 * time : 2020/6/23 10:53
 * describe :
 */
object Api {

    private fun getBaseUrl(): String {
        return "https://gank.io/api/v2"
    }

    suspend fun getMeiziList(pageCount: Int = 1): List<MeiziModel> {
        return RxHttp.get("${getBaseUrl()}/data/category/Girl/type/Girl/page/${pageCount}/count/12")
            .toResponseList<MeiziModel>()
            .await()
    }

    suspend fun getUpDate(id: String, apiToken: String): String {
        return RxHttp.get("http://api.bq04.com/apps/latest/${id}")
            .add("api_token", apiToken)
            .toStr()
            .await()
    }

    suspend fun getGanHuoList(
        category: String,
        pageCount: Int = 1,
        type: String
    ): List<MeiziModel> {
        return RxHttp.get("${getBaseUrl()}/data/category/${category}/type/${type}/page/${pageCount}/count/12")
            .toResponseList<MeiziModel>()
            .await()
    }

    suspend fun getBanner(): List<BannerModel> {
        return RxHttp.get("${getBaseUrl()}/banners")
            .toResponseList<BannerModel>()
            .await()
    }

    suspend fun getSearch(
        searchContent: String,
        pageCount: Int = 1
    ): List<MeiziModel> {
        return RxHttp.get("${getBaseUrl()}/search/${searchContent}/category/All/type/All/page/${pageCount}/count/12")
            .toResponseList<MeiziModel>()
            .await()
    }
}