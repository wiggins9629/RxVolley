package com.wiggins.rxvolley.utils;

import android.view.View;

import com.kymjs.core.bitmap.client.BitmapCore;
import com.wiggins.rxvolley.R;

/**
 * @Description Bitmap工具类
 * @Author 一花一世界
 * @Time 2017/2/24 15:40
 */
public class BitmapUtil {

    /**
     * BitmapCore 图片加载
     *
     * @param view   网络请求接口url
     * @param picUrl 要显示图片的view
     */
    public static void loadImage(View view, String picUrl) {
        new BitmapCore.Builder()
                .url(picUrl)
                .view(view)
                .loadResId(R.drawable.image_load)
                .errorResId(R.drawable.image_error)
                .doTask();
    }
}
