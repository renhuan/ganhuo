package com.android.ganhuo.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.android.ganhuo.MainActivity
import com.android.ganhuo.R
import com.android.ganhuo.base.BaseFragment
import com.android.ganhuo.http.Api
import com.android.ganhuo.model.event.RefreshEvent
import com.example.myapplication.model.Data
import com.example.myapplication.model.MeiziModel
import com.renhuan.okhttplib.eventbus.Event
import com.renhuan.okhttplib.utils.Renhuan
import com.wuyr.activitymessenger.get
import kotlinx.android.synthetic.main.fragment_meizi.*
import me.jingbin.library.adapter.BaseByViewHolder
import me.jingbin.library.adapter.BaseRecyclerAdapter

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
                object : BaseRecyclerAdapter<Data>(R.layout.item_ganhuo, arrayListOf()) {
                    override fun bindView(holder: BaseByViewHolder<Data>?, bean: Data?, position: Int) {
                        val iv = holder?.getView<ImageView>(R.id.iv)
                        Renhuan.glide(iv!!, bean?.getmImage()!!)
                        holder.setText(R.id.tv_title, bean.title)
                        holder.setText(R.id.tv_des, bean.desc)
                        holder.setText(R.id.tv_author, "@author ${bean.author}")
                        holder.setText(R.id.tv_time, bean.getmPublishedAt())
                    }
                }
            }

    override fun inflaterLayout(): Int {
        return R.layout.fragment_ganhuo
    }

    override fun init(view: View) {
        super.init(view)
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

    private fun refresh(pageCount: Int) {
        Api.getGanHuoAndroidList(category, pageCount, type!!, this@GanHuoFragment)
    }


//    override fun lazyLoad() {
//        super.lazyLoad()
//        println("-------------" + type + " lazyLoad")
//        recyclerView.isRefreshing = true
//    }

    override fun onError() {
        super.onError()
        recyclerView.isRefreshing = false
        recyclerView.loadMoreComplete()
    }

    override fun <T> onSuccess(data: T) {
        super.onSuccess(data)
        if (data is MeiziModel) {
            data.data.apply {
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
        }
    }

    override val isRegisterEventBus: Boolean
        get() = true

    private var category = MainActivity.CATEGORY_GANHUO

    override fun receiveEvent(event: Event<*>) {
        super.receiveEvent(event)
        event.data?.let {
            if (it is RefreshEvent) {
                category = it.category
                recyclerView?.isRefreshing = true
            }
        }
    }
}
