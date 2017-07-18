package com.example.yuzelli.fooddelivered.view.activity;

import android.content.Context;
import android.content.Intent;
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
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;


public class RegisterActivity extends BaseActivity {


    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.user_phone)
    EditText userPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.user_name)
    EditText username;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.bt_register)
    Button btRegister;

    private RegisterHandler handler;


    @Override
    protected int layoutInit() {
        return R.layout.activity_register;
    }

    @Override
    protected void binEvent() {
        tvCenter.setText("用户注册");
        tvRight.setVisibility(View.GONE);
        handler = new RegisterHandler();

    }

    @Override
    protected void fillData() {

    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }


    @OnClick({R.id.iv_left, R.id.bt_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.bt_register:
                String mobile = userPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String un = username.getText().toString().trim();
                if (verification(mobile, password, confirmPassword,un)) {
                    doRegisterAction(mobile, password,un);
                }
                break;
        }
    }

    //101：注册成功；102注册失败；103密码或者电话号码为空；104已注册过了
    private void doRegisterAction(String mobile, String password,String username) {
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("password", password);
        map.put("stName", username);
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.REGISTER_USER, map, new OkHttpClientManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                showToast("获取数据失败！");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject json = new JSONObject(result);
                int code = json.optInt("code");
                if (code == 101) {
                    showToast("注册成功！");
                    handler.sendEmptyMessage(ConstantsUtils.REGISTER_GET_DATA);
                } else if (code == 102) {
                    showToast("注册失败！");
                } else if (code == 103) {
                    showToast("密码或者电话号码为空！");
                } else if (code == 104) {
                    showToast("已注册过了！");
                } else {
                    showToast("数据获取失败！");
                }
            }
        });

    }

    private boolean verification(String mobile, String password, String confirmPassword,String u_n) {
        boolean flag = true;
        if (u_n.equals("")) {
            showToast("请输入用户名！");
            flag = false;
        }
        if (mobile.equals("")) {
            showToast("请输入手机号！");
            flag = false;
        }
        if (password.equals("")) {
            showToast("请输入密码！");
            flag = false;
        }
        if (confirmPassword.equals("")) {
            showToast("请再次输入密码！");
            flag = false;
        }


        if (!OtherUtils.isPhoneEnable(mobile)) {
            showToast("输入手机号有误！");
            flag = false;
        }
        if (!password.equals(confirmPassword)) {
            showToast("两次输入密码不一致！");
            flag = false;
        }

        return flag;
    }

    class RegisterHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.REGISTER_GET_DATA:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
