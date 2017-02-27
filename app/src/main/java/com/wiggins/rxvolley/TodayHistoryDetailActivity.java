package com.wiggins.rxvolley;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kymjs.rxvolley.client.HttpParams;
import com.wiggins.rxvolley.adapter.QueryDetailPicAdapter;
import com.wiggins.rxvolley.base.BaseActivity;
import com.wiggins.rxvolley.bean.QueryDetailPicUrl;
import com.wiggins.rxvolley.bean.ResultDesc;
import com.wiggins.rxvolley.bean.TodayHistoryQueryDetail;
import com.wiggins.rxvolley.http.HttpRequest;
import com.wiggins.rxvolley.listener.HttpListener;
import com.wiggins.rxvolley.utils.Constant;
import com.wiggins.rxvolley.utils.ToastUtil;
import com.wiggins.rxvolley.utils.UIUtils;
import com.wiggins.rxvolley.widget.TitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 事件列表详情
 * @Author 一花一世界
 * @Time 2017/2/24 14:44
 */

public class TodayHistoryDetailActivity extends BaseActivity {

    private TodayHistoryDetailActivity mActivity = null;
    private TitleView titleView;
    private TextView mTvDetailTitle;
    private TextView mTvDetailContent;
    private TextView mTvEmpty;
    private ListView mLvData;

    private List<TodayHistoryQueryDetail> todayHistoryQueryDetail;
    private List<QueryDetailPicUrl> queryDetailPicUrls;
    private QueryDetailPicAdapter queryDetailPicAdapter;
    private Gson gson = null;

    private String e_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_history_detail);
        mActivity = this;

        initView();
        initData();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleView);
        titleView.setAppTitle(UIUtils.getString(R.string.event_list_details));
        titleView.setLeftImgOnClickListener();
        mTvDetailTitle = (TextView) findViewById(R.id.tv_detail_title);
        mTvDetailContent = (TextView) findViewById(R.id.tv_detail_content);
        mTvDetailContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mLvData = (ListView) findViewById(R.id.lv_data);
        mLvData.setEmptyView(mTvEmpty);
    }

    private void initData() {
        if (gson == null) {
            gson = new Gson();
        }
        if (todayHistoryQueryDetail == null) {
            todayHistoryQueryDetail = new ArrayList<>();
        }
        if (queryDetailPicUrls == null) {
            queryDetailPicUrls = new ArrayList<>();
        }
        if (queryDetailPicAdapter == null) {
            queryDetailPicAdapter = new QueryDetailPicAdapter(queryDetailPicUrls, mActivity);
            mLvData.setAdapter(queryDetailPicAdapter);
        } else {
            queryDetailPicAdapter.notifyDataSetChanged();
        }

        Intent intent = getIntent();
        if (intent != null) {
            e_id = intent.getStringExtra("e_id");
        }

        getTodayHistoryQueryDetail();
    }

    /**
     * @Description 历史上的今天 事件列表详情
     */
    private void getTodayHistoryQueryDetail() {
        HttpParams params = new HttpParams();
        params.put("key", Constant.APP_KEY);
        params.put("e_id", e_id);

        HttpRequest.post(Constant.queryDetail, params, new HttpListener() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                todayHistoryQueryDetail.clear();
                queryDetailPicUrls.clear();
                mTvDetailTitle.setText("");
                mTvDetailContent.setText("");
                if (resultDesc.getError_code() == 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultDesc.getResult());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            TodayHistoryQueryDetail bean = gson.fromJson(jsonObject.toString(), TodayHistoryQueryDetail.class);
                            todayHistoryQueryDetail.add(bean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mTvDetailTitle.setText(todayHistoryQueryDetail.get(0).getTitle().trim());
                    mTvDetailContent.setText(todayHistoryQueryDetail.get(0).getContent().trim());
                    queryDetailPicUrls.addAll(todayHistoryQueryDetail.get(0).getPicUrl());
                    queryDetailPicAdapter.setData(queryDetailPicUrls);

                    Log.e(Constant.LOG_TAG, "历史上的今天 - 事件详情:" + todayHistoryQueryDetail.toString());
                    Log.e(Constant.LOG_TAG, "历史上的今天 - 事件详情 - 图片详情:" + queryDetailPicUrls.toString());
                } else {
                    ToastUtil.showText(resultDesc.getReason());
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showText(strMsg);
            }
        });
    }
}
