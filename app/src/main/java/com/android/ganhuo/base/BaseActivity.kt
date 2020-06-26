package com.android.ganhuo.base

import android.os.Bundle
import com.renhuan.okhttplib.base.RBaseActivity

abstract class BaseActivity : RBaseActivity() {

    override fun init(savedInstanceState: Bundle?) {
    }

    override fun <T> onSuccess(data: T) {
    }

    override fun onError() {
    }
}