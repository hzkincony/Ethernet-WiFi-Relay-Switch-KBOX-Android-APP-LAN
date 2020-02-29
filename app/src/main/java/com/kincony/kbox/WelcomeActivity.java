package com.kincony.kbox;

import android.os.Handler;

import com.kincony.kbox.ui.MainActivity;
import com.kincony.kbox.ui.base.BaseActivity;

public class WelcomeActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.Companion.start(WelcomeActivity.this);
                finish();
            }
        }, 1500);
    }
}
