package com.android.ganhuo.http

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.JsonParser
import com.lxj.xpopup.XPopup
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.Response
import com.renhuan.okhttplib.http.RBaseOkHttp
import com.renhuan.okhttplib.http.RBaseOkHttpImp
import com.renhuan.okhttplib.utils.Renhuan


/**
 * created by renhuan
 * time : 2020/5/15 10:10
 * describe : T 如果为Any 则类class为ANY_CLASS 否者为T.class
 */
open class BaseOkHttp<T> : RBaseOkHttp<T>() {

    companion object {
        const val SUCCESS = 100
    }

    private val loading by lazy {
        XPopup.Builder(Renhuan.getCurrentActivity())
            .hasShadowBg(false)
            .dismissOnTouchOutside(false)
            .asLoading()
//            .bindLayout(R.layout.loading)
    }

    override fun onRError(rBaseOkHttpImp: RBaseOkHttpImp?) {
        rBaseOkHttpImp?.onError()
    }


    override fun onRSuccess(cls: Class<*>, response: Response<String>?, rBaseOkHttpImp: RBaseOkHttpImp?) {
        try {
            response?.body()?.let {
                when (JsonParser.parseString(it).asJsonObject.get("status").asInt) {
                    SUCCESS -> {
                        rBaseOkHttpImp?.onSuccess(GsonUtils.fromJson(it, cls))
                    }
                    else -> {
                        rBaseOkHttpImp?.onError()
                    }
                }
            }
        } catch (e: Exception) {
            rBaseOkHttpImp?.onError()
            Renhuan.toast("数据解析错误${e.message}")
        }
    }


    override fun setHttpHead(httpHeaders: HttpHeaders): HttpHeaders? {
        return httpHeaders
    }

    override fun showLoading() {
        loading.show()
    }

    override fun hideLoading() {
        loading.dismiss()
    }
}