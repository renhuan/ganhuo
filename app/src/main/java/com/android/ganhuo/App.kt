package com.android.ganhuo

import com.renhuan.okhttplib.RApp
import com.tencent.bugly.crashreport.CrashReport


class App : RApp() {

    override fun init() {
        CrashReport.initCrashReport(applicationContext, "03e0f02fd6", BuildConfig.DEBUG)
    }

    override fun getSpiderTheme(): Int {
        return 0
    }

}