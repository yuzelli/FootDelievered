package com.example.yuzelli.fooddelivered.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseActivity;
import com.example.yuzelli.fooddelivered.bean.NowOrderBean;
import com.example.yuzelli.fooddelivered.bean.OrderBean;
import com.example.yuzelli.fooddelivered.bean.UserInfo;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.SharePreferencesUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

public class OrderDetailActivity extends BaseActivity {


    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_order_create_time)
    TextView tvOrderCreateTime;
    @BindView(R.id.img_order_info)
    ImageView imgOrderInfo;
    @BindView(R.id.btn_receive_order)
    Button btnReceiveOrder;

    private OrderBean mOrder;
    private Context context;
    private OrderDetailHandler handler;
    @Override
    protected int layoutInit() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void binEvent() {
        tvCenter.setText("订单详情");
        tvRight.setVisibility(View.GONE);
        handler = new OrderDetailHandler();
        Intent intent = getIntent();
        mOrder = (OrderBean) intent.getSerializableExtra("order");
        context = this;


    }

    public static void actionStart(Context context , OrderBean order){
        Intent intent = new Intent(context,OrderDetailActivity.class);
        intent.putExtra("order",order);
        context.startActivity(intent);
    }

    @Override
    protected void fillData() {

    }

    @OnClick({R.id.iv_left, R.id.btn_receive_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_receive_order:
                doReceiveAction();
                break;
        }
    }

    private void doReceiveAction() {
        UserInfo user = (UserInfo) SharePreferencesUtil.readObject(context,ConstantsUtils.SP_LOGIN_USER_INFO);
        Map<String,String> map = new HashMap<>();
        map.put("order_id",mOrder.getOrder_id());
        map.put("stId",user.getStId());
        map.put("mobile",user.getMobile());
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.RECEIVE_ORDER, map, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {

                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        JSONObject json = new JSONObject(result);
                        int code = json.optInt("code");
                        if (code == 108) {
                            showToast("接单成功成功！");
                            Gson gson = new Gson();
                            NowOrderBean now = gson.fromJson(json.optString("order_info"),NowOrderBean.class);
                            SharePreferencesUtil.saveObject(context,ConstantsUtils.SP_NOW_ORDER_INFO,now);
                            handler.sendEmptyMessage(ConstantsUtils.RECEIVE_ORDER_LIST_GET_DATA);
                        }else {
                            showToast("获取数据失败！");
                        }

                    }
                }
        );
    }

    class OrderDetailHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case ConstantsUtils.RECEIVE_ORDER_LIST_GET_DATA:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
