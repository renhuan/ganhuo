package com.android.ganhuo.http

import com.android.ganhuo.model.BannerModel
import com.example.myapplication.model.MeiziModel
import com.lzy.okgo.cache.CacheMode
import com.renhuan.okhttplib.http.RBaseOkHttpImp

/**
 * created by renhuan
 * time : 2020/6/23 10:53
 * describe :
 */
object Api {

    private fun getBaseUrl(): String {
        return "https://gank.io/api/v2"
    }

    fun getMeiziList(pageCount: Int = 1, baseCall: RBaseOkHttpImp) {
        object : BaseOkHttp<MeiziModel>() {}
            .setUrl("${getBaseUrl()}/data/category/Girl/type/Girl/page/${pageCount}/count/12")
            .setCallBack(baseCall)
            .isShowLoading(false)
            .get()
    }

    fun getGanHuoAndroidList(
        category: String,
        pageCount: Int = 1,
        type: String,
        baseCall: RBaseOkHttpImp
    ) {
        object : BaseOkHttp<MeiziModel>() {}
            .setUrl("${getBaseUrl()}/data/category/${category}/type/${type}/page/${pageCount}/count/12")
            .setCallBack(baseCall)
            .isShowLoading(false)
            .get()
    }

    fun getBanner(baseCall: RBaseOkHttpImp) {
        object : BaseOkHttp<BannerModel>() {}
            .setUrl("${getBaseUrl()}/banners")
            .setCallBack(baseCall)
            .setCache(CacheMode.FIRST_CACHE_THEN_REQUEST)
            .isShowLoading(false)
            .get()
    }

    fun getSearch(
        searchContent: String,
        pageCount: Int = 1,
        baseCall: RBaseOkHttpImp
    ) {
        object : BaseOkHttp<MeiziModel>() {}
            .setUrl("${getBaseUrl()}/search/${searchContent}/category/All/type/All/page/${pageCount}/count/12")
            .setCallBack(baseCall)
            .get()
    }
}