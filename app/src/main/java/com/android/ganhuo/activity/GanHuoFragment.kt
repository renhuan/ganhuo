package com.android.ganhuo.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.android.ganhuo.MainActivity
import com.android.ganhuo.R
import com.android.ganhuo.base.BaseFragment
import com.android.ganhuo.http.Api
import com.android.ganhuo.model.event.RefreshEvent
import com.example.myapplication.model.MeiziModel
import com.renhuan.okhttplib.utils.Renhuan
import com.wuyr.activitymessenger.get
import kotlinx.android.synthetic.main.fragment_meizi.*
import me.jingbin.library.adapter.BaseByViewHolder
import me.jingbin.library.adapter.BaseRecyclerAdapter
import rxhttp.wrapper.cahce.CacheMode

class GanHuoFragment : BaseFragment() {

    private val type
            by lazy {
                arguments?.get<String>("type")
            }

    companion object {
        fun getInstance(type: String): GanHuoFragment {
            return GanHuoFragment().apply {
                arguments = Bundle().apply {
                    this.putString("type", type)
                }
            }
        }
    }

    private var pageCount = 1 //页码

    private val mAdapter
            by lazy {
                object : BaseRecyclerAdapter<MeiziModel>(R.layout.item_ganhuo, arrayListOf()) {
                    override fun bindView(holder: BaseByViewHolder<MeiziModel>?, bean: MeiziModel?, position: Int) {
                        val iv = holder?.getView<ImageView>(R.id.iv)
                        Renhuan.glide(iv!!, bean?.getImage_()!!)
                        holder.setText(R.id.tv_title, bean.title)
                        holder.setText(R.id.tv_des, bean.desc)
                        holder.setText(R.id.tv_author, "@${bean.author}")
                        holder.setText(R.id.tv_time, bean.getPublishedAt_())
                    }
                }
            }

    override fun inflaterLayout(): Int {
        return R.layout.fragment_ganhuo
    }

    override fun initRequest() {
        super.initRequest()
        refresh(1.apply { pageCount = this }, CacheMode.ONLY_CACHE)
    }

    override fun initView(view: View) {
        super.initView(view)
        recyclerView?.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            setOnItemClickListener { _, position ->
                WebActivity.startAction(activity!!, mAdapter.getItemData(position).url)
            }
            setOnRefreshListener {
                refresh(1.apply { pageCount = this })
            }
            setOnLoadMoreListener {
                refresh(++pageCount)
            }
            recyclerView.isRefreshing = true
        }
    }

    private fun refresh(pageCount: Int, cacheMode: CacheMode = CacheMode.NETWORK_SUCCESS_WRITE_CACHE) {
        rxScope(
            false,
            action = {
                Api.getMeiziList(category, pageCount, type!!, cacheMode).apply {
                    if (pageCount == 1) {
                        mAdapter.clear()
                        recyclerView.isRefreshing = false
                    }
                    recyclerView.loadMoreComplete()
                    if (this.isEmpty()) {
                        recyclerView.loadMoreEnd()
                    } else {
                        mAdapter.addData(this)
                    }
                }
            },
            onError = {
                recyclerView.isRefreshing = false
                recyclerView.loadMoreComplete()
            }
        )
    }

    override val isRegisterEventBus: Boolean
        get() = true

    private var category = MainActivity.CATEGORY_GANHUO

    override fun receiveEvent(event: Any) {
        super.receiveEvent(event)
        event.let {
            if (it is RefreshEvent) {
                category = it.category
                recyclerView?.isRefreshing = true
            }
        }
    }
}
