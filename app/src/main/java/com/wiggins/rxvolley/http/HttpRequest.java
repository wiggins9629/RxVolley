package com.wiggins.rxvolley.http;

import android.os.NetworkOnMainThreadException;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.kymjs.rxvolley.client.ProgressListener;
import com.wiggins.rxvolley.R;
import com.wiggins.rxvolley.bean.ResultDesc;
import com.wiggins.rxvolley.listener.HttpListener;
import com.wiggins.rxvolley.utils.StringUtil;
import com.wiggins.rxvolley.utils.UIUtils;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * @Description 网络请求封装
 * @Author 一花一世界
 * @Time 2017/2/23 16:16
 */

public class HttpRequest {

    /**
     * 自定义Http请求回调监听
     */
    public static HttpListener httpListener;

    /**
     * get 请求
     *
     * @param url      访问服务器地址
     * @param listener 自定义Http请求回调
     */
    public static void get(String url, HttpListener listener) {
        new RxVolley.Builder().url(url).callback(getHttpCallback(listener)).doTask();
    }

    /**
     * get 请求
     *
     * @param url      访问服务器地址
     * @param params   请求参数
     * @param listener 自定义Http请求回调
     */
    public static void get(String url, HttpParams params, HttpListener listener) {
        new RxVolley.Builder().url(url).params(params).callback(getHttpCallback(listener)).doTask();
    }

    /**
     * post 请求
     *
     * @param url      访问服务器地址
     * @param params   请求参数
     * @param listener 自定义Http请求回调
     */
    public static void post(String url, HttpParams params, HttpListener listener) {
        new RxVolley.Builder().url(url).params(params).httpMethod(RxVolley.Method.POST).callback(getHttpCallback(listener)).doTask();
    }

    /**
     * post 请求
     *
     * @param url      访问服务器地址
     * @param params   请求参数
     * @param listener 自定义Http请求回调
     */
    public static void postProgress(String url, HttpParams params, HttpListener listener) {
        new RxVolley.Builder().url(url).params(params).progressListener(getProgressListener(listener)).httpMethod(RxVolley.Method.POST).callback(getHttpCallback(listener)).doTask();
    }

    /**
     * 下载
     *
     * @param storeFilePath 本地存储绝对路径
     * @param url           要下载的文件的url
     * @param listener      自定义Http请求回调
     */
    public static void download(String storeFilePath, String url, HttpListener listener) {
        RxVolley.download(storeFilePath, url, getProgressListener(listener), getHttpCallback(listener));
    }

    /**
     * Http请求回调类
     *
     * @param listener 自定义Http请求回调
     * @return
     */
    public static HttpCallback getHttpCallback(final HttpListener listener) {
        httpListener = listener;
        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                //网络请求成功时的回调
                httpListener.onSuccess(getReturnData(t));
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                //网络请求失败时的回调,例如无网络，服务器异常等
                httpListener.onFailure(errorNo, strMsg);
            }
        };
        return httpCallback;
    }

    /**
     * Http上传或下载时进度回调
     *
     * @param listener 自定义Http请求回调
     * @return
     */
    public static ProgressListener getProgressListener(final HttpListener listener) {
        httpListener = listener;
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                //transferredBytes 进度  totalSize 总量
                httpListener.onProgress(transferredBytes, totalSize);
            }
        };
        return progressListener;
    }

    /**
     * 返回数据解析
     *
     * @param result 请求返回字符串
     * @return
     */
    public static ResultDesc getReturnData(String result) {
        ResultDesc resultDesc = null;

        if (StringUtil.isEmpty(result)) {
            //返回数据为空
            resultDesc = dataRestructuring(-1, UIUtils.getString(R.string.back_abnormal_results), "");
            return resultDesc;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            //返回码
            int error_code = jsonObject.getInt("error_code");
            //返回说明
            String reason = jsonObject.getString("reason");
            //返回数据
            String resultData = jsonObject.getString("result");

            resultDesc = dataRestructuring(error_code, reason, resultData);
        } catch (JSONException e) {
            resultDesc = dataRestructuring(-1, ExceptionCode(e), "");
        }

        return resultDesc;
    }

    /**
     * 数据重组
     *
     * @param error_code 返回码
     * @param reason     返回说明
     * @param resultData 返回数据
     * @return
     */
    public static ResultDesc dataRestructuring(int error_code, String reason, String resultData) {
        ResultDesc resultDesc = new ResultDesc();
        resultDesc.setError_code(error_code);
        resultDesc.setReason(reason);
        resultDesc.setResult(resultData);
        return resultDesc;
    }

    /**
     * 异常处理
     *
     * @param e 各类异常
     * @return
     */
    public static String ExceptionCode(Exception e) {
        if (e instanceof NetworkOnMainThreadException) {
            // 主线程中访问网络时异常
            // Android在4.0之前的版本支持在主线程中访问网络，但是在4.0以后对这部分程序进行了优化，也就是说访问网络的代码不能写在主线程中了。
            // 解决方法：采用多线程、异步加载的方式加载数据
            return UIUtils.getString(R.string.main_thread_access_network_exception);
        } else if (e instanceof SocketTimeoutException) {
            // 服务器响应超时
            return UIUtils.getString(R.string.server_response_timeout);
        } else if (e instanceof ConnectTimeoutException) {
            // 服务器请求超时
            return UIUtils.getString(R.string.server_request_timeout);
        } else if (e instanceof IOException) {
            // I/O异常
            return UIUtils.getString(R.string.io_exception);
        } else if (e instanceof JSONException) {
            // JSON格式转换异常
            return UIUtils.getString(R.string.json_format_conversion_exception);
        } else {
            // 其他异常
            return UIUtils.getString(R.string.back_abnormal_results);
        }
    }
}
