package com.example.yuzelli.fooddelivered.view.activity;

import android.content.Intent;
import android.widget.TextView;


import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseActivity;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;

import butterknife.BindView;

/**
 * Created by 51644 on 2017/7/13.
 */

public class OnTrialActivity extends BaseActivity {
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;
    String time = "2017-8-2 12:00";

    @Override
    protected int layoutInit() {
        return R.layout.activity_ontail;
    }

    @Override
    protected void binEvent() {
         textView3.postDelayed(new Runnable() {
             @Override
             public void run() {
                if(OtherUtils.date2TimeStamp(time)>System.currentTimeMillis()/1000){
                    startActivity(new Intent(OnTrialActivity.this,SplashActivity.class));
                    finish();
                }else {
                    textView4.setText("已过期");
                    showToast("已过期");
                }
             }
         },3*1000);
    }

    @Override
    protected void fillData() {

    }


}
