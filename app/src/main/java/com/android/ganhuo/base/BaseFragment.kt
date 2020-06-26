package com.android.ganhuo.base

import android.view.View
import com.renhuan.okhttplib.base.RBaseFragment

abstract class BaseFragment : RBaseFragment() {

    override fun init(view: View) {

    }

    override fun lazyLoad() {
    }

    override fun <T> onSuccess(data: T) {
    }

    override fun onError() {
    }
}