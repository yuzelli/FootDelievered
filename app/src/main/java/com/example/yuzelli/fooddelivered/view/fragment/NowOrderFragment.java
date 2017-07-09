package com.example.yuzelli.fooddelivered.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseFragment;
import com.example.yuzelli.fooddelivered.bean.NowOrderBean;
import com.example.yuzelli.fooddelivered.bean.UserInfo;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;
import com.example.yuzelli.fooddelivered.utils.SharePreferencesUtil;
import com.google.gson.Gson;
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
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by 51644 on 2017/7/6.
 * 正在进行的订单
 */

public class NowOrderFragment extends BaseFragment {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
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
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;
    private Context context;
    private NowOrderBean now;
    private NowOrderFragmentHandler handler;

    DisplayImageOptions options  = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.def2)        // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.def2)  // 设置图片Uri为空或是错误的时候显示的图片
            // .cacheInMemory(true)//设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
            .build();
    @Override
    protected int layoutInit() {
        return R.layout.fragment_now_order;
    }

    @Override
    protected void bindEvent(View v) {
        ivLeft.setVisibility(View.GONE);
        tvCenter.setText("当前任务");
        tvRight.setVisibility(View.GONE);
        context = getActivity();
        handler = new NowOrderFragmentHandler();
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
        now = (NowOrderBean) SharePreferencesUtil.readObject(context, ConstantsUtils.SP_NOW_ORDER_INFO);
        if (now != null) {
            upDataOrder(now);
            emptyView.setVisibility(View.GONE);
            llHaveOrder.setVisibility(View.VISIBLE);
        } else {
            getNowOrder();
        }
    }

    private void upDataOrder(NowOrderBean now) {
//        String hour = now.getConfirm_time().substring(14,16);
//        String mine = now.getConfirm_time().substring(15,17);
//        String miao = now.getConfirm_time().substring(18,20);
//        int ordercurrt = Integer.valueOf(hour)*60*60+Integer.valueOf(mine)+Integer.valueOf(miao);
        long order_currt = OtherUtils.date2TimeStamp(now.getConfirm_time());
        long time = System.currentTimeMillis() / 1000;
        current_time = all_time - (int) (time - order_currt);
        ;
        ImageLoader.getInstance().loadImage(now.getImg_url(),options, new ImageLoadingListener() {
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

    private void getNowOrder() {
        UserInfo user = (UserInfo) SharePreferencesUtil.readObject(context, ConstantsUtils.SP_LOGIN_USER_INFO);
        Map<String, String> map = new HashMap<>();
        map.put("stId", user.getStId());
        map.put("mobile", user.getMobile());
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.GET_NOW_ORDER, map, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {

                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        if (!result.contains("\"code\":113")) {
                            Gson gson = new Gson();
                            NowOrderBean now = gson.fromJson(result, NowOrderBean.class);
                            SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_NOW_ORDER_INFO, now);
                            Message msg = new Message();
                            msg.obj = now;
                            msg.what = ConstantsUtils.GET_NOW_ORDER_LIST_GET_DATA;
                            handler.sendMessage(msg);
                        } else {
                            llHaveOrder.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            tvHint.setText("当前无订单！");
                        }

                    }
                }
        );
    }

    @OnClick(R.id.btn_finish_order)
    public void onViewClicked() {
        if (now != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    class NowOrderFragmentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.SENG_COUNT_DOWN_MESSAGE:
                    current_time--;
                    if (tvCountDown==null){
                        return;
                    }
                    tvCountDown.setText("倒计时：" + setShowCountDownText(current_time));

                    if (current_time > 0) {
                        Message message = handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);
                        handler.sendMessageDelayed(message, 1000);      // send message
                    } else {
                        tvCountDown.setText("订单超时：" + setShowCountDownText(-current_time));
                        Message message = handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);
                        handler.sendMessageDelayed(message, 1000);
                    }
                    break;
                case ConstantsUtils.GET_NOW_ORDER_LIST_GET_DATA:
                    NowOrderBean mNow = (NowOrderBean) msg.obj;
                    upDataOrder(mNow);
                    break;
                case ConstantsUtils.FINISH_NOW_ORDER_LIST_GET_DATA:
                    SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_NOW_ORDER_INFO, null);
                    emptyView.setVisibility(View.VISIBLE);
                    llHaveOrder.setVisibility(View.GONE);
                    break;

            }
        }
    }

    private String setShowCountDownText(int time) {
        StringBuffer buffer = new StringBuffer();
        int feng = time / 60;
        int min = time % 60;
        return buffer.append(feng + "分").append(min + "秒").toString();

    }

}
