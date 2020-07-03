package com.android.ganhuo.activity

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.android.ganhuo.R
import com.android.ganhuo.base.BaseActivity
import com.android.ganhuo.http.Api
import com.blankj.utilcode.util.KeyboardUtils
import com.example.myapplication.model.MeiziModel
import com.renhuan.okhttplib.utils.Renhuan
import com.renhuan.okhttplib.utils.checkEmpty
import com.renhuan.okhttplib.utils.text
import com.wuyr.activitymessenger.ActivityMessenger
import com.wuyr.activitymessenger.get
import kotlinx.android.synthetic.main.activity_main.toolBar
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_meizi.recyclerView
import me.jingbin.library.adapter.BaseByViewHolder
import me.jingbin.library.adapter.BaseRecyclerAdapter

class SearchActivity : BaseActivity(), TextWatcher {

    //揭露(进入)动画
    private var mAnimIn: Animator? = null

    //反揭露(退出)动画
    private var mAnimOut: Animator? = null

    private val clickX by lazy { intent.get<Int>(CLICK_X) }

    private val clickY by lazy { intent.get<Int>(CLICK_Y) }

    private val mAdapter
            by lazy {
                object : BaseRecyclerAdapter<MeiziModel>(R.layout.item_ganhuo, arrayListOf()) {
                    override fun bindView(holder: BaseByViewHolder<MeiziModel>?, bean: MeiziModel?, position: Int) {
                        val iv = holder?.getView<ImageView>(R.id.iv)
                        Renhuan.glide(iv!!, bean?.getImage_()!!)
                        holder.setText(R.id.tv_title, bean.title)
                        holder.setText(R.id.tv_des, bean.desc)
                        holder.setText(R.id.tv_author, "@author ${bean.author}")
                        holder.setText(R.id.tv_time, bean.getPublishedAt_())
                    }
                }
            }


    private var pageCount = 1


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        startAnimator()
        initRecyclerView()
        btn_search.setOnClickListener {
            refresh(1.apply { pageCount = this })
        }
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        et.addTextChangedListener(this)
    }

    //进入动画
    private fun startAnimator() {
        root.let {
            it.visibility = View.INVISIBLE
            it.post {
                it.visibility = View.VISIBLE
                mAnimIn = ViewAnimationUtils.createCircularReveal(
                    it,
                    clickX!!,
                    clickY!!,
                    0f,
                    it.height.toFloat()
                ).apply {
                    interpolator = LinearInterpolator()
                    addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            et.requestFocus()
                            KeyboardUtils.showSoftInput()
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }

                    })
                    start()
                }
            }
        }
    }

    //退出动画
    private fun stopAnimator() {
        window.decorView.let {
            it.post {
                mAnimOut = ViewAnimationUtils.createCircularReveal(
                    it,
                    clickX!!,
                    clickY!!,
                    it.height.toFloat(),
                    0f
                ).apply {
                    interpolator = LinearInterpolator()
                    addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            it.visibility = View.INVISIBLE
                            finishs()
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }

                    })
                    start()
                }
            }
        }
    }

    private fun refresh(pageCount: Int) {
        et.checkEmpty("请输入关键词搜索") ?: return
        isRefreshable(true)
        rxScope {
            Api.getSearch(et.text(), pageCount).apply {
                if (pageCount == 1) {
                    mAdapter.clear()
                    recyclerView.isRefreshing = false
                }
                recyclerView.isStateViewEnabled = false
                recyclerView.loadMoreComplete()
                if (this.isEmpty()) {
                    recyclerView.loadMoreEnd()
                } else {
                    mAdapter.addData(this)
                }
            }
        }
    }

    private fun initRecyclerView() {

        recyclerView?.apply {
            adapter = mAdapter
            setStateView(R.layout.layout_empty)
            setHasFixedSize(true)
            setOnItemClickListener { _, position ->
                WebActivity.startAction(this@SearchActivity, mAdapter.getItemData(position).url)
            }
            setOnRefreshListener {
                refresh(1.apply { pageCount = this })
            }
            setOnLoadMoreListener {
                refresh(++pageCount)
            }
            isRefreshable(false)
        }
    }

    private fun isRefreshable(b: Boolean) {
        recyclerView.isRefreshEnabled = b
        recyclerView.isLoadMoreEnabled = b
    }

    override fun inflaterLayout(): Int? {
        return R.layout.activity_search
    }

    private fun finishs() {
        super.onBackPressed()
    }

    override fun onBackPressed() {
        KeyboardUtils.hideSoftInput(this)
        stopAnimator()
    }


    override fun onDestroy() {
        super.onDestroy()
        mAnimIn?.removeAllListeners()
        mAnimIn?.cancel()
        mAnimOut?.removeAllListeners()
        mAnimOut?.cancel()
    }

    companion object {
        const val CLICK_X = "CLICK_X"
        const val CLICK_Y = "CLICK_Y"
        fun startAction(context: Context, x: Int, y: Int) {
            ActivityMessenger.startActivity<SearchActivity>(context, CLICK_X to x, CLICK_Y to y)
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s?.isEmpty()!!) {
            mAdapter.setNewData(null)
            isRefreshable(false)
            recyclerView?.setStateView(R.layout.layout_empty)
        }
    }
}