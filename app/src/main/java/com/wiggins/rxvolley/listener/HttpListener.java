package com.wiggins.rxvolley.listener;

import com.wiggins.rxvolley.bean.ResultDesc;

/**
 * @Description 自定义Http请求回调类
 * @Author 一花一世界
 * @Time 2017/2/23 16:56
 */

public abstract class HttpListener {

    /**
     * Http请求成功时回调
     *
     * @param resultDesc HttpRequest返回信息
     */
    public void onSuccess(ResultDesc resultDesc) {
    }

    /**
     * Http请求失败时回调
     *
     * @param errorNo 错误码
     * @param strMsg  错误原因
     */
    public void onFailure(int errorNo, String strMsg) {
    }

    /**
     * Http上传或下载时进度回调
     *
     * @param transferredBytes 进度
     * @param totalSize        总量
     */
    public void onProgress(long transferredBytes, long totalSize) {
    }
}
