package com.android.ganhuo.activity

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.android.ganhuo.R
import com.android.ganhuo.base.BaseFragment
import com.android.ganhuo.http.Api
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.myapplication.model.Data
import com.example.myapplication.model.MeiziModel
import com.example.myapplication.view.MyImageViewerPopupView
import com.lxj.xpopup.XPopup
import com.renhuan.okhttplib.utils.Renhuan
import kotlinx.android.synthetic.main.fragment_meizi.*
import me.jingbin.library.adapter.BaseByViewHolder
import me.jingbin.library.adapter.BaseRecyclerAdapter


/**
 * A simple [Fragment] subclass.
 */
class MeiziFragment : BaseFragment() {

    /**
     * 图片集合 用于全屏浏览图片
     */
    private var listUrl = arrayListOf<String>()

    /**
     * 加载的页码数
     */
    private var pageCount = 1

    private val mAdapter by lazy {
        object : BaseRecyclerAdapter<Data>(R.layout.item_meizi, arrayListOf()) {
            override fun bindView(holder: BaseByViewHolder<Data>?, bean: Data?, position: Int) {
                val iv = holder?.getView<ImageView>(R.id.image_view)
                Glide
                    .with(Renhuan.getContext())
                    .load(bean?.getmImage())
                    .apply(RequestOptions().apply {
                        override(SIZE_ORIGINAL)
                        placeholder(R.drawable.loading)
                        error(R.drawable.empty)
                    })
                    .into(iv!!)

                holder.setText(R.id.tv, bean?.desc)
            }
        }
    }

    override fun inflaterLayout(): Int {
        return R.layout.fragment_meizi
    }

    override fun init(view: View) {
        super.init(view)
        initRecycerView()
    }

    private fun initRecycerView() {
        recyclerView?.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            setOnItemClickListener { v, position ->
                XPopup.Builder(activity)
                    .asCustom(
                        MyImageViewerPopupView(
                            activity!!,
                            v.findViewById(R.id.image_view),
                            position,
                            listUrl,
                            mAdapter.data
                        )
                    )
                    .show()
            }
            setOnRefreshListener {
                refresh(1.apply { pageCount = this })
            }
            setOnLoadMoreListener {
                refresh(++pageCount)
            }
            isRefreshing = true
        }
    }


    private fun refresh(pageCount: Int) {
        Api.getMeiziList(pageCount, this@MeiziFragment)
    }

    override fun <T> onSuccess(data: T) {
        super.onSuccess(data)
        if (data is MeiziModel) {
            data.data.apply {
                if (pageCount == 1) {
                    listUrl.clear()
                    mAdapter.clear()
                    recyclerView.isRefreshing = false
                }
                recyclerView.loadMoreComplete()
                if (this.isEmpty()) {
                    recyclerView.loadMoreEnd()
                } else {
                    mAdapter.addData(this)
                    listUrl.addAll(map { it.getmImage() })
                }
            }
        }
    }

    override fun onError() {
        super.onError()
        recyclerView.isRefreshing = false
        recyclerView.loadMoreComplete()
    }
}
