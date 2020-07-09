package com.android.ganhuo.http

import android.accounts.NetworkErrorException
import com.android.ganhuo.model.BannerModel
import com.example.myapplication.model.MeiziModel
import rxhttp.retry
import rxhttp.toStr
import rxhttp.tryAwait
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseList
import java.net.ConnectException


/**
 * created by renhuan
 * time : 2020/6/23 10:53
 * describe :
 */
object Api {

    private fun getBaseUrl(): String {
        return "https://gank.io/api/v2"
    }

    suspend fun getUpDate(id: String, apiToken: String): String {
        return RxHttp.get("http://api.bq04.com/apps/latest/${id}")
            .add("api_token", apiToken)
            .toStr()
            .await()
    }

    /** 妹子 干货 */
    suspend fun getMeiziList(
        category: String,
        pageCount: Int = 1,
        type: String,
        cacheMode: CacheMode
    ): List<MeiziModel> {
        return RxHttp.get("${getBaseUrl()}/data/category/${category}/type/${type}/page/${pageCount}/count/12")
            .setCacheMode(cacheMode)
            .toResponseList<MeiziModel>()
            .await()
    }

    suspend fun getBanner(cacheMode: CacheMode = CacheMode.NETWORK_SUCCESS_WRITE_CACHE): List<BannerModel>? {
        return RxHttp.get("${getBaseUrl()}/banners")
            .setCacheMode(cacheMode)
            .toResponseList<BannerModel>()
            .tryAwait()
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