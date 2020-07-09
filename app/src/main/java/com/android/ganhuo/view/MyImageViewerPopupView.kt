package com.android.ganhuo.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.ImageView
import com.android.ganhuo.R
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.PermissionUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.myapplication.model.MeiziModel
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.interfaces.XPopupImageLoader
import com.renhuan.okhttplib.utils.Renhuan
import com.rxlife.coroutine.RxLifeScope
import kotlinx.android.synthetic.main.popup_meizi.view.*
import kotlinx.coroutines.Job
import rxhttp.toDownload
import rxhttp.wrapper.param.RxHttp
import java.io.File

/**
 * created by renhuan
 * time : 2020/6/21 20:37
 * describe :
 */
class MyImageViewerPopupView(
    context: Context,
    imageView: ImageView,
    pos: Int,
    private var listUrl: List<String>,
    private var listData: List<MeiziModel>
) : ImageViewerPopupView(context) {

    private var mContext: Context? = null
    private var mPositon = 0

    init {
        mPositon = pos
        mContext = context
        setSrcView(imageView, mPositon)
        setImageUrls(listUrl as List<Any>)
        setXPopupImageLoader(ImageLoader())
        isShowIndicator(false)
        isShowSaveButton(false)
        setSrcViewUpdateListener { _, position ->
            mPositon = position
            setData(position)
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_meizi
    }

    override fun onCreate() {
        super.onCreate()
        setData(mPositon)
        fab.setOnClickListener {
            showSharePopop()
        }
    }

    private fun showSharePopop() {
        XPopup.Builder(mContext)
            .atView(fab)
            .asAttachList(
                arrayOf("下载", "分享"),
                intArrayOf(R.drawable.ic_baseline_cloud_download_24, R.drawable.ic_baseline_share_24)
            ) { position, _ ->
                when (position) {
                    0 -> downloadImage(listUrl[mPositon])
                    1 -> downloadImage(listUrl[mPositon], true)
                }
            }
            .show()
    }

    private val loading by lazy {
        XPopup.Builder(mContext)
            .dismissOnTouchOutside(false)
            .asLoading("加载中...")
    }


    private var job: Job? = null

    fun cancelScope() {
        job?.cancel()
    }

    private fun downloadImage(url: String, isShare: Boolean = false) {
        PermissionUtils
            .permission(PermissionConstants.STORAGE)
            .rationale { _, shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    loading.show()
                    job = RxLifeScope().launch {
                        val filePath = RxHttp.get(url)
                            .toDownload(PathUtils.getExternalAppCachePath() + "/${url.substring(url.lastIndexOf("/"))}")
                            .await()
                        loading.dismiss()
                        try {
                            Renhuan.toast("图片已保存${filePath}")
                            //插入相册
                            val uri =
                                context.contentResolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    ContentValues().apply {
                                        put(MediaStore.Images.Media.DATA, filePath)
                                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                    })

                            //通知相册更新
                            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

                            //是否分享
                            if (isShare) {
                                mContext?.startActivity(IntentUtils.getShareImageIntent("", filePath))
                            }
                        } catch (e: Exception) {
                            Renhuan.toast("图片下载错误")
                        }
                    }
                }

                override fun onDenied(deniedForever: MutableList<String>, denied: MutableList<String>) {
                }
            })
            .request()
    }

    private fun setData(pos: Int) {
        tv.text = listData[pos].desc
        tv_author.text = "@${listData[pos].author}"
        tv_time.text = listData[pos].getPublishedAt_()
        tv_page.text = "${pos + 1}/${listData.size}"
    }

    private class ImageLoader : XPopupImageLoader {
        override fun loadImage(position: Int, uri: Any, imageView: ImageView) {
            Glide
                .with(imageView)
                .load(uri)
                .apply(RequestOptions().apply {
                    override(Target.SIZE_ORIGINAL)
                    placeholder(R.drawable.loading)
                    error(R.drawable.empty)
                })
                .into(imageView)
        }

        override fun getImageFile(context: Context, uri: Any): File? {
            return null
        }
    }
}