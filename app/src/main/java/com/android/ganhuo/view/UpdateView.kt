package com.android.ganhuo.view

import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.UIData
import com.android.ganhuo.model.UpdateModel
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.renhuan.okhttplib.utils.Renhuan
import java.lang.Exception

/**
 * created by renhuan
 * time : 2020/6/26 17:33
 * describe :
 */
object UpdateView {
    private val api_token = ""
    private val id = ""

    fun check() {
        OkGo.get<String>("http://api.bq04.com/apps/latest/${id}")
            .params("api_token", api_token)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        response?.body()?.let {
                            val json = GsonUtils.fromJson(it, UpdateModel::class.java)

                            if (AppUtils.getAppVersionCode() < json.version.toInt()) {
                                attempUpdate(json)
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            })
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