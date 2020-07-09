package com.android.ganhuo

import android.animation.ValueAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import androidx.viewpager.widget.ViewPager
import com.android.ganhuo.activity.GanHuoFragment
import com.android.ganhuo.activity.MeiziFragment
import com.android.ganhuo.activity.SearchActivity
import com.android.ganhuo.activity.WebActivity
import com.android.ganhuo.adapter.BannerAdapter
import com.android.ganhuo.base.BaseActivity
import com.android.ganhuo.http.Api
import com.android.ganhuo.model.BannerModel
import com.android.ganhuo.model.event.RefreshEvent
import com.android.ganhuo.view.UpdateUtils
import com.blankj.utilcode.util.BarUtils
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.renhuan.okhttplib.adapter.MyFragmentPagerAdapter
import com.renhuan.okhttplib.eventbus.REventBus
import com.renhuan.okhttplib.utils.Renhuan
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Cache
import rxhttp.wrapper.cahce.CacheMode
import kotlin.math.abs

class MainActivity : BaseActivity(), ViewPager.OnPageChangeListener, OnTabSelectListener {

    private var smallIndex = 0

    companion object {
        const val CATEGORY_ARTICLE = "Article"
        const val CATEGORY_GANHUO = "GanHuo"
    }

    private val mBannerView
            by lazy {
                findViewById<BannerViewPager<BannerModel, BannerAdapter.NetViewHolder>>(R.id.bannerView)
            }

    private val bigValueAnimator
            by lazy {
                ValueAnimator.ofFloat(28f, 35f).apply { duration = 100 }
            }

    private val smallValueAnimator
            by lazy {
                ValueAnimator.ofFloat(35f, 28f).apply { duration = 100 }
            }

    private val mTitleList
            by lazy {
                arrayListOf(
                    "妹子",
                    "Android",
                    "Flutter",
                    "iOS",
                    "前端",
                    "后端",
                    "App"
                )
            }

    private val vpAdapter
            by lazy {
                MyFragmentPagerAdapter(
                    supportFragmentManager,
                    arrayListOf(
                        MeiziFragment(),
                        GanHuoFragment.getInstance("Android"),
                        GanHuoFragment.getInstance("Flutter"),
                        GanHuoFragment.getInstance("iOS"),
                        GanHuoFragment.getInstance("frontend"),
                        GanHuoFragment.getInstance("backend"),
                        GanHuoFragment.getInstance("app")
                    ),
                    mTitleList
                )
            }

    override fun inflaterLayout(): Int? {
        return R.layout.activity_main
    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        //检查更新
        UpdateUtils.check()

        //statusBar init
        BarUtils.setStatusBarColor(this, Renhuan.getColor(R.color.transparent))
        BarUtils.addMarginTopEqualStatusBarHeight(toolBar)

        //menu  init
        setSupportActionBar(toolBar)
        toolBar.setOnMenuItemClickListener {
            SearchActivity.startAction(
                this,
                toolBar.width,
                toolBar.height
            )
            true
        }

        //viewPage init
        vp?.apply {
            offscreenPageLimit = mTitleList.size
            adapter = vpAdapter
            tab_type?.setViewPager(this)
            bigAnimator(0)
            addOnPageChangeListener(this@MainActivity)
        }

        //category init
        tab_category?.setTabData(
            arrayListOf(
                object : CustomTabEntity {
                    override fun getTabUnselectedIcon(): Int {
                        return 0
                    }

                    override fun getTabSelectedIcon(): Int {
                        return 0
                    }

                    override fun getTabTitle(): String {
                        return "干货"
                    }

                },
                object : CustomTabEntity {
                    override fun getTabUnselectedIcon(): Int {
                        return 0
                    }

                    override fun getTabSelectedIcon(): Int {
                        return 0
                    }

                    override fun getTabTitle(): String {
                        return "文章"
                    }
                }
            )
        )
        tab_category.setOnTabSelectListener(this)

        //banner init
        mBannerView.apply {
            adapter = BannerAdapter()
            setOnPageClickListener {
                WebActivity.startAction(this@MainActivity, mBannerView.data[it].url)
            }
            create()
        }
    }

    override fun initRequest() {
        super.initRequest()
        rxScope(false) {
            Api.getBanner(CacheMode.ONLY_CACHE).let {
                mBannerView.refreshData(it)
            }
            Api.getBanner().let {
                mBannerView.refreshData(it)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolBar.inflateMenu(R.menu.index_menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun bigAnimator(index: Int) {
        tab_type?.getTitleView(index)?.typeface = Typeface.DEFAULT_BOLD
        bigValueAnimator?.apply {
            addUpdateListener { animation ->
                tab_type.getTitleView(index)
                    .setTextSize(TypedValue.COMPLEX_UNIT_MM, animation.animatedValue as Float)
            }
            start()
        }
    }


    private fun smallAnimator(index: Int) {
        tab_type?.getTitleView(index)?.typeface = Typeface.DEFAULT
        smallValueAnimator?.apply {
            addUpdateListener { animation ->
                tab_type.getTitleView(index)
                    .setTextSize(TypedValue.COMPLEX_UNIT_MM, animation.animatedValue as Float)
            }
            start()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        UpdateUtils.cancelScope()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onResume() {
        super.onResume()
        bannerView?.startLoop()
    }

    override fun onStop() {
        super.onStop()
        bannerView.stopLoop()
    }

    override fun onPageSelected(position: Int) {
        appBar?.setExpanded(false)
        bigValueAnimator?.removeAllUpdateListeners()
        smallValueAnimator?.removeAllUpdateListeners()
        mTitleList.forEachIndexed { index, _ ->
            if (index == position) {
                bigAnimator(index)
                smallAnimator(smallIndex)
                smallIndex = position
            }
        }
    }

    override fun onTabSelect(position: Int) {
        REventBus.sendEvent(RefreshEvent(if (position == 0) CATEGORY_GANHUO else CATEGORY_ARTICLE))
    }

    override fun onTabReselect(position: Int) {
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}
