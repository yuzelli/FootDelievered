package com.example.yuzelli.fooddelivered.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseActivity;
import com.example.yuzelli.fooddelivered.bean.UserInfo;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;
import com.example.yuzelli.fooddelivered.utils.SharePreferencesUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;


public class LoginActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;

    @BindView(R.id.user_phone)
    EditText userPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.bt_login)
    Button btLogin;

    private LoginHandler handler;
    private Context context;


    @Override
    protected int layoutInit() {
        return R.layout.activity_login;
    }

    @Override
    protected void binEvent() {
        ivLeft.setVisibility(View.GONE);
        tvRight.setText("注册");
        tvCenter.setText("用户登录");
        handler = new LoginHandler();
        context = this;

    }

    @Override
    protected void fillData() {

    }


    @OnClick({R.id.tv_right, R.id.bt_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                RegisterActivity.startAction(context);
                break;
            case R.id.bt_login:
                String mobile = userPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (verification(mobile, password)) {
                    doLoginAction(mobile, password);
                }
                break;
        }
    }

    public static void startAction(Context context){
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * code =201表示电话号码不对，code =202表示密码错误，code =203表示未知错误，
     */
    private void doLoginAction(String mobile, String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("password", password);
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.USER_LOGIN, map, new OkHttpClientManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                showToast("获取数据失败！");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                int code = json.optInt("code");
                if (code == 200) {
                    showToast("登录成功！");
                    handler.sendEmptyMessage(ConstantsUtils.LOGIN_GET_DATA);
                } else if (code == 201) {
                    showToast("用户不存在！");
                } else if (code == 202) {
                    showToast("密码错误！");
                }else {
                    showToast("获取数据失败！");
                }
            }
        });
    }

    private boolean verification(String mobile, String password) {
        boolean flag = true;
        if (mobile.equals("")) {
            showToast("请输入手机号！");
            flag = false;
        }
        if (password.equals("")) {
            showToast("请输入密码！");
            flag = false;
        }

        if (!OtherUtils.isPhoneEnable(mobile)){
            showToast("输入手机号有误！");
            flag = false;
        }


            return flag;

    }

    class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.LOGIN_GET_DATA:
                    String mobile = userPhone.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_LOGIN_USER_INFO, new UserInfo(mobile, password));
                    MainActivity.actionStart(context);
                    break;
                default:
                    break;
            }
        }
    }
}

