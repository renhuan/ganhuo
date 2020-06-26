package com.example.myapplication.view

import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.MediaStore
import android.widget.ImageView
import com.android.ganhuo.R
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.PermissionUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.myapplication.model.Data
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.interfaces.XPopupImageLoader
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Response
import com.renhuan.okhttplib.utils.Renhuan
import kotlinx.android.synthetic.main.popup_meizi.view.*
import java.io.File

/**
 * created by renhuan
 * time : 2020/6/21 20:37
 * describe :
 */
class MyImageViewerPopupView(
    context: Context,
    imageView: ImageView,
    private var pos: Int,
    private var listUrl: List<String>,
    private var listData: List<Data>
) : ImageViewerPopupView(context) {

    private var mContext: Context? = null
    private val valueAnimator by lazy { ValueAnimator.ofInt(100, 255) }
    private var mPos = 0

    init {
        mPos = pos
        mContext = context
        setSrcView(imageView, mPos)
        setImageUrls(listUrl as List<Any>)
        setXPopupImageLoader(ImageLoader())
        isShowIndicator(false)
        isShowSaveButton(false)
        setSrcViewUpdateListener { _, position ->
            mPos = position
            setData(position)
            setAnimator()
        }
    }

    private fun setAnimator() {
        valueAnimator.removeAllUpdateListeners()
        valueAnimator.apply {
            addUpdateListener {
                tv.setTextColor(Color.argb(it.animatedValue as Int, 255, 255, 255))
                tv_author.setTextColor(Color.argb(it.animatedValue as Int, 255, 255, 255))
                tv_time.setTextColor(Color.argb(it.animatedValue as Int, 255, 255, 255))
            }
        }.start()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.popup_meizi
    }

    override fun onCreate() {
        super.onCreate()
        setData(mPos)
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
                    0 -> downloadImage(listUrl[mPos])
                    1 -> downloadImage(listUrl[mPos], true)
                }
            }
            .show()
    }

    private val loading by lazy {
        XPopup.Builder(mContext)
            .dismissOnTouchOutside(false)
            .asLoading("下载中...")
    }

    private fun downloadImage(url: String, isShare: Boolean = false) {
        PermissionUtils
            .permission(PermissionConstants.STORAGE)
            .rationale { _, shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    loading.show()
                    OkGo.get<File>(url).execute(object : FileCallback() {
                        override fun onSuccess(response: Response<File>?) {
                            loading.dismiss()
                            try {
                                response?.body()?.let {
                                    Renhuan.toast("图片已保存${it.absolutePath}")

                                    //插入相册
                                    val uri =
                                        context.contentResolver.insert(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            ContentValues().apply {
                                                put(MediaStore.Images.Media.DATA, it.absolutePath)
                                                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                            })

                                    //通知相册更新
                                    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

                                    //是否分享
                                    if (isShare) {
                                        mContext?.startActivity(IntentUtils.getShareImageIntent("", it.absolutePath))
                                    }
                                }
                            } catch (e: Exception) {
                                Renhuan.toast("图片下载错误")
                            }
                        }
                    })
                }

                override fun onDenied(deniedForever: MutableList<String>, denied: MutableList<String>) {
                }
            })
            .request()
    }

    private fun setData(pos: Int) {
        tv.text = listData[pos].desc
        tv_author.text = "@author ${listData[pos].author}"
        tv_time.text = listData[pos].getmPublishedAt()
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