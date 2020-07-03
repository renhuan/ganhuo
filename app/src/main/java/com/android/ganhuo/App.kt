package com.android.ganhuo

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SDCardUtils
import com.readystatesoftware.chuck.ChuckInterceptor
import com.renhuan.okhttplib.RApp
import com.renhuan.okhttplib.utils.Renhuan
import com.tencent.bugly.crashreport.CrashReport
import okhttp3.OkHttpClient
import okhttp3.internal.connection.ConnectInterceptor
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import java.io.File


class App : RApp() {

    override fun init() {
        CrashReport.initCrashReport(applicationContext, "03e0f02fd6", BuildConfig.DEBUG)
    }

    override fun getSpiderTheme(): Int {
        return 0
    }

}