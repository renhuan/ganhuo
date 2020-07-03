package com.android.ganhuo.view

import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.UIData
import com.android.ganhuo.http.Api
import com.android.ganhuo.model.UpdateModel
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.renhuan.okhttplib.utils.Renhuan
import com.rxlife.coroutine.RxLifeScope
import kotlinx.coroutines.Job
import okhttp3.Response
import java.lang.Exception

/**
 * created by renhuan
 * time : 2020/6/26 17:33
 * describe :
 */
object UpdateUtils {
    private val api_token = ""
    private val id = ""
    private var job: Job? = null
    fun check() {
        job = RxLifeScope().launch {
            Api.getUpDate(id, api_token).let {
                val json = GsonUtils.fromJson(it, UpdateModel::class.java)
                if (AppUtils.getAppVersionCode() < json.version.toInt()) {
                    attempUpdate(json)
                }
            }
        }
    }

    fun cancelScope() {
        job?.cancel()
    }

    private fun attempUpdate(it: UpdateModel) {
        AllenVersionChecker
            .getInstance()
            .downloadOnly(
                UIData
                    .create()
                    .setTitle("发现新版本")
                    .setContent("v${it.versionShort}\n\n${it.changelog}")
                    .setDownloadUrl(it.direct_install_url)
            )
            .setForceRedownload(true)
            .executeMission(Renhuan.getContext())
    }
}