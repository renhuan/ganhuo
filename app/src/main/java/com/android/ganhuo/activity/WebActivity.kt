package com.android.ganhuo.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.widget.LinearLayout
import com.android.ganhuo.R
import com.android.ganhuo.base.BaseActivity
import com.just.agentweb.*
import com.renhuan.okhttplib.utils.Renhuan
import com.wuyr.activitymessenger.ActivityMessenger
import com.wuyr.activitymessenger.get
import kotlinx.android.synthetic.main.activity_web.*
import me.jessyan.autosize.AutoSizeCompat

class WebActivity : BaseActivity() {


    private val url
            by lazy {
                intent.get<String>(URL)
            }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            toolBar?.title = title
        }
    }
    private val mWebViewClient: WebViewClient = object : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.proceed()
        }
    }
    private val mAgentWebPre
            by lazy {
                AgentWeb.with(this)
                    .setAgentWebParent(container as LinearLayout, LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setWebViewClient(mWebViewClient)
                    .setWebChromeClient(mWebChromeClient)
                    .setMainFrameErrorView(R.layout.error_page, -1)
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他应用时，弹窗咨询用户是否前往其他应用
                    .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                    .createAgentWeb()
                    .ready()
            }


    private var mAgentWeb: AgentWeb? = null

    companion object {
        const val URL = "url"
        fun startAction(context: Context, url: String) {
            ActivityMessenger.startActivity<WebActivity>(context, URL to url)
        }
    }

    override fun inflaterLayout(): Int? {
        return R.layout.activity_web
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mAgentWeb = mAgentWebPre.go(url)

        setSupportActionBar(toolBar)

        toolBar.setNavigationOnClickListener {
            mAgentWeb?.let {
                if (!it.back()) {
                    finish()
                }
            }
        }

        toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> {
                    mAgentWeb?.urlLoader?.reload() // 刷新
                    true
                }
                R.id.copy -> {
                    Renhuan.copy(mAgentWeb?.webCreator?.webView?.url!!)
                    true
                }
                R.id.open -> {
                    if (mAgentWeb?.webCreator?.webView?.url?.contains("http")!!) {
                        openBrowser(mAgentWeb?.webCreator?.webView?.url)
                    } else {
                        Renhuan.toast("此页面不能在浏览器打开")
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolBar.inflateMenu(R.menu.web_menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (mAgentWeb?.handleKeyEvent(keyCode, event)!!) true else super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()

    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAgentWeb?.webLifeCycle?.onDestroy()
    }

    override fun getResources(): Resources {
        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()))
        return super.getResources()
    }

    /**
     * 打开浏览器
     */
    private fun openBrowser(targetUrl: String?) {
        targetUrl?.let {
            startActivity(Intent().apply {
                action = "android.intent.action.VIEW"
                data = Uri.parse(it)
            })
        }
    }
}