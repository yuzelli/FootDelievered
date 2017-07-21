package com.example.yuzelli.fooddelivered.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseActivity;
import com.example.yuzelli.fooddelivered.bean.NowOrderBean;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;
import com.example.yuzelli.fooddelivered.view.fragment.NowOrderFragment;
import com.example.yuzelli.fooddelivered.view.fragment.PersonalFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

public class NowOrderDetailActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_send_time)
    TextView tv_send_time;
    @BindView(R.id.tv_count_down)
    TextView tvCountDown;
    @BindView(R.id.img_order_info)
    ImageView imgOrderInfo;
    @BindView(R.id.btn_finish_order)
    Button btnFinishOrder;


    private final static int all_time = 60 * 60;
    private static int current_time;
    @BindView(R.id.ll_have_order)
    LinearLayout llHaveOrder;
    @BindView(R.id.tv_order_sn)
    TextView tvOrderSn;
    @BindView(R.id.tv_state)
    TextView tvState;

    private Context context;
    private NowOrderBean now;
    private NowODHander handler;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.def2)        // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.def2)  // 设置图片Uri为空或是错误的时候显示的图片
            // .cacheInMemory(true)//设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
            .build();

    @Override
    protected int layoutInit() {
        return R.layout.activity_now_order_detail;
    }

    @Override
    protected void binEvent() {
        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCenter.setText("订单任务");

        tvRight.setVisibility(View.GONE);
        context = this;
        handler = new NowODHander();
        now = (NowOrderBean) getIntent().getSerializableExtra("now");
        if (now != null) {
            upDataOrder(now);
            llHaveOrder.setVisibility(View.VISIBLE);
        }
        beginTimer();
    }


    private void beginTimer() {
        Message message = handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);     // Message
        handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);
        handler.sendMessageDelayed(message, 1000);
    }

    private boolean isFirstFlag = false;

    @Override
    protected void fillData() {

    }

    private void upDataOrder(NowOrderBean now) {
       tvOrderSn.setText("订单号："+now.getOrder_sn());
        if (now.getOrder_type()!=null){
            if (now.getOrder_type().equals("1")){
                tvState.setText("美团");
            }else if (now.getOrder_type().equals("2")){
                tvState.setText("饿了么");
            }else if (now.getOrder_type().equals("3")){
                tvState.setText("百度外卖");
            }else if (now.getOrder_type().equals("4")){
                tvState.setText("口碑");
            }
        }else {
            tvState.setText("");
        }
        long order_currt = OtherUtils.date2TimeStamp(now.getSended_time());
        long time = System.currentTimeMillis() / 1000;
        current_time = (int) (order_currt - time);
        tv_phone.setText(now.getMobile());
        tv_send_time.setText(now.getSended_time());
        ImageLoader.getInstance().loadImage(now.getImg_url(), options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {


            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                imgOrderInfo.setImageBitmap(bitmap);

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    public static void startAction(Context context, NowOrderBean now) {
        Intent intent = new Intent(context, NowOrderDetailActivity.class);
        intent.putExtra("now", now);
        context.startActivity(intent);
    }


    @OnClick(R.id.btn_finish_order)
    public void onViewClicked() {
        if (now != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            builder.setMessage("是否已经完成订单？");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doFinishOrder();
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        } else {
            showToast("当前没有订单哎！");
        }
    }

    private void doFinishOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("order_id", now.getOrder_id());
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.FINISH_NOW_ORDER, map, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {

                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        JSONObject json = new JSONObject(result);
                        int code = json.optInt("code");
                        if (code == 114) {
                            showToast("订单完成！");
                            handler.sendEmptyMessage(ConstantsUtils.FINISH_NOW_ORDER_LIST_GET_DATA);
                        } else if (code == 115) {
                            showToast("完成操作失败！");
                        } else if (code == 203) {
                            showToast("获取数据失败！");
                        }
                    }
                }
        );
    }


    class NowODHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.SENG_COUNT_DOWN_MESSAGE:
                    current_time--;
                    if (tvCountDown == null) {
                        return;
                    }
                    tvCountDown.setText("倒计时：" + OtherUtils.setShowCountDownText(current_time));

                    if (current_time > 0) {
                        Message message = handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);
                        handler.sendMessageDelayed(message, 1000);      // send message
                    } else {
                        tvCountDown.setText("订单超时：" + OtherUtils.setShowCountDownText(-current_time));
                        Message message = handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);
                        handler.sendMessageDelayed(message, 1000);
                    }
                    break;
                case ConstantsUtils.FINISH_NOW_ORDER_LIST_GET_DATA:
                    NowOrderFragment.isNeedUpDataFlag = true;
                    PersonalFragment.isNeedGetNewOrderListData = true;
                    finish();
                    break;

            }
        }
    }



}
