package com.android.ganhuo.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.ganhuo.R
import com.android.ganhuo.model.BannerModel
import com.renhuan.okhttplib.utils.Renhuan
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * created by renhuan
 * time : 2020/6/24 11:26
 * describe :
 */
class BannerAdapter : BaseBannerAdapter<BannerModel, BannerAdapter.NetViewHolder>() {
    class NetViewHolder(itemView: View) : BaseViewHolder<BannerModel>(itemView) {
        override fun bindData(data: BannerModel, position: Int, pageSize: Int) {
            val tv = findView<TextView>(R.id.tv_title)
            val iv = findView<ImageView>(R.id.iv)
            Renhuan.glide(iv, data.image)
            tv.text = data.title
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_banner
    }

    override fun createViewHolder(itemView: View?, viewType: Int): NetViewHolder {
        return NetViewHolder(itemView!!)
    }

    override fun onBind(holder: NetViewHolder?, data: BannerModel?, position: Int, pageSize: Int) {
        data?.let {
            holder?.bindData(it, position, pageSize)
        }
    }
}