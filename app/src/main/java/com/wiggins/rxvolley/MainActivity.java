package com.wiggins.rxvolley;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kymjs.rxvolley.client.HttpParams;
import com.wiggins.rxvolley.adapter.TodayHistoryQueryAdapter;
import com.wiggins.rxvolley.base.BaseActivity;
import com.wiggins.rxvolley.bean.ResultDesc;
import com.wiggins.rxvolley.bean.TodayHistoryQuery;
import com.wiggins.rxvolley.http.HttpRequest;
import com.wiggins.rxvolley.listener.HttpListener;
import com.wiggins.rxvolley.utils.Constant;
import com.wiggins.rxvolley.utils.StringUtil;
import com.wiggins.rxvolley.utils.ToastUtil;
import com.wiggins.rxvolley.utils.UIUtils;
import com.wiggins.rxvolley.widget.TitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private MainActivity mActivity = null;
    private TitleView titleView;
    private EditText mEdtData;
    private Button mBtnQuery;
    private TextView mTvEmpty;
    private ListView mLvData;

    private List<TodayHistoryQuery> todayHistoryQuery;
    private TodayHistoryQueryAdapter todayHistoryQueryAdapter;
    private Gson gson = null;
    private String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        initView();
        initData();
        setLinstener();
    }

    private void initData() {
        if (gson == null) {
            gson = new Gson();
        }
        if (todayHistoryQuery == null) {
            todayHistoryQuery = new ArrayList<>();
        }
        if (todayHistoryQueryAdapter == null) {
            todayHistoryQueryAdapter = new TodayHistoryQueryAdapter(todayHistoryQuery, mActivity);
            mLvData.setAdapter(todayHistoryQueryAdapter);
        } else {
            todayHistoryQueryAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleView);
        titleView.setAppTitle(UIUtils.getString(R.string.event_list));
        titleView.setLeftImageVisibility(View.GONE);
        mEdtData = (EditText) findViewById(R.id.edt_data);
        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mLvData = (ListView) findViewById(R.id.lv_data);
        mLvData.setEmptyView(mTvEmpty);
    }

    private void setLinstener() {
        mBtnQuery.setOnClickListener(this);
        mLvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(mActivity, TodayHistoryDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("e_id", String.valueOf(todayHistoryQuery.get(position).getE_id()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * @Description 历史上的今天 事件列表
     */
    private void getTodayHistoryQuery() {
        HttpParams params = new HttpParams();
        params.put("key", Constant.APP_KEY);
        params.put("date", data);

        HttpRequest.get(Constant.queryEvent, params, new HttpListener() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                todayHistoryQuery.clear();
                if (resultDesc.getError_code() == 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultDesc.getResult());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            TodayHistoryQuery bean = gson.fromJson(jsonObject.toString(), TodayHistoryQuery.class);
                            todayHistoryQuery.add(bean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    todayHistoryQueryAdapter.setData(todayHistoryQuery);
                    Log.e(Constant.LOG_TAG, "历史上的今天 - 事件列表:" + todayHistoryQuery.toString());
                } else {
                    todayHistoryQueryAdapter.setData(todayHistoryQuery);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_query:
                data = mEdtData.getText().toString().trim();
                if (StringUtil.isEmpty(data)) {
                    ToastUtil.showText(UIUtils.getString(R.string.query_date_not_empty));
                    return;
                }
                getTodayHistoryQuery();
                break;
        }
    }
}