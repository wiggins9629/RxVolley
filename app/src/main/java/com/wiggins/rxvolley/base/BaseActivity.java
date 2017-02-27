package com.wiggins.rxvolley.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * @Description 所有Activity的基类
 * @Author 一花一世界
 * @Time 2017/2/23 10:40
 */

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.finishActivity(this);
    }
}
