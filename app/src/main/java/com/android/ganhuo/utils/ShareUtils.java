package com.android.ganhuo.utils;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.BuildConfig;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * created by renhuan
 * time : 2020/5/27 10:51
 * describe : 分享工具类
 */

/**
 * sendIntent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");//微信朋友
 * sendIntent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");//微信朋友圈，仅支持分享图片
 * sendIntent.setClassName("com.tencent.mobileqq", "cooperation.qqfav.widget.QfavJumpActivity");//保存到QQ收藏
 * sendIntent.setClassName("com.tencent.mobileqq", "cooperation.qlink.QlinkShareJumpActivity");//QQ面对面快传
 * sendIntent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.qfileJumpActivity");//传给我的电脑
 * sendIntent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");//QQ好友或QQ群
 */
public class ShareUtils {
    private static Context context;

    /**
     * 分享到微信
     */
    public static void shareToWx(Context context, String path) {
        ShareUtils.context = context;
        try {
            Intent intent = new Intent();
            Uri uri = getUri(new File(path));
            ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(componentName);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort("未检测到微信");
            e.printStackTrace();
        }
    }

    /**
     * 分享信息到朋友圈
     */
    public static void shareToWxFriend(Context context, String path) {
        ShareUtils.context = context;
        try {
            Intent intent = new Intent();
            Uri uri = getUri(new File(path));
            ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(componentName);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort("未检测到微信");
            e.printStackTrace();
        }
    }

    /**
     * 分享到telegram
     */
    public static void shareToTelegram(Context context, String path) {
        ShareUtils.context = context;
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            Uri uri = getUri(new File(path));
            intent.setPackage("org.telegram.messenger");
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort("未检测到Telegram");
            e.printStackTrace();
        }
    }


    private static Uri getUri(File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 保存图片到SD卡
     */
    public static String savePhotoToSDCard(Context context, Bitmap photoBitmap, String path, String photoName) {
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            FileOutputStream fileOutputStream;

            try {
                //保存图片
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File photoFile = new File(path, photoName + ".png");
                fileOutputStream = new FileOutputStream(photoFile);
                photoBitmap.compress(Bitmap.CompressFormat.PNG, 50, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                ToastUtils.showShort("图片已保存：" + photoFile.getPath());

                //插入相册
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, photoFile.getPath());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                //通知更新
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                return photoFile.getPath();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }
}
