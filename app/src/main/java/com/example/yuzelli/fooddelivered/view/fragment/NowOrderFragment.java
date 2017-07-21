package com.example.yuzelli.fooddelivered.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseFragment;
import com.example.yuzelli.fooddelivered.bean.NowOrderBean;
import com.example.yuzelli.fooddelivered.bean.UserInfo;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.CommonAdapter;
import com.example.yuzelli.fooddelivered.utils.GsonUtils;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;
import com.example.yuzelli.fooddelivered.utils.SharePreferencesUtil;
import com.example.yuzelli.fooddelivered.utils.ViewHolder;
import com.example.yuzelli.fooddelivered.view.activity.NowOrderDetailActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
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
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;
    @BindView(R.id.lv_order)
    PullToRefreshListView lvOrder;

    private static Context context;
    private static NowOrderFragment mfragment;
    private List<NowOrderBean> nowList;
    private NowOrderFragmentHandler handler;
    public static boolean isNeedUpDataFlag = false;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
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
        mfragment = this;
        tvCenter.setText("当前任务列表");
        tvRight.setVisibility(View.GONE);
        context = getActivity();
        handler = new NowOrderFragmentHandler();
        getNowOrder();

        Message message = handler.obtainMessage(ConstantsUtils.SENG_GET_NOW_ORDER_MESSAGE);     // Message
        handler.sendMessageDelayed(message, 60*1000);
    }

    @Override
    protected void fillData() {
        if (isNeedUpDataFlag) {
            getNowOrder();
        }
        showNotail();
    }



    /**
     * 显示通知
     */
    public static void showNotail() {
        if (context==null){
            return;
        }
        final String message = (String) SharePreferencesUtil.readObject(context, ConstantsUtils.SP_TOAST_USER_INFO);
        if (message!=null&&!message.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);// 构建

            builder.setTitle("通知");
            builder.setMessage(message);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mfragment!=null){
                        mfragment.getNowOrder();
                    }
                    SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_TOAST_USER_INFO,null);
                }
            });
            builder.show();
            SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_TOAST_USER_INFO,null);
        }
    }

    public void getNowOrder() {
        UserInfo user = (UserInfo) SharePreferencesUtil.readObject(context, ConstantsUtils.SP_LOGIN_USER_INFO);
        Map<String, String> map = new HashMap<>();
        map.put("stId", user.getStId());
        map.put("mobile", user.getMobile());
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.GET_NOW_ORDER, map, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        lvOrder.onRefreshComplete();
                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        lvOrder.onRefreshComplete();
                        if (!result.contains("\"code\":113")) {

                            nowList = GsonUtils.jsonToArrayList(result, NowOrderBean.class);
//                            SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_NOW_ORDER_INFO, nowList);
                            handler.sendEmptyMessage(ConstantsUtils.GET_NOW_ORDER_LIST_GET_DATA);
                        } else {
                            if (nowList != null) {
                                nowList.clear();
                            }

                            upDataOrder();
                            tvHint.setText("当前无订单！");
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
                case ConstantsUtils.GET_NOW_ORDER_LIST_GET_DATA:
                    isNeedUpDataFlag = false;
                    upDataOrder();
                    break;
                case ConstantsUtils.SENG_GET_NOW_ORDER_MESSAGE:
                    getNowOrder();
                    Message message = handler.obtainMessage(ConstantsUtils.SENG_GET_NOW_ORDER_MESSAGE);
                    handler.sendMessageDelayed(message, 60*1000);
                    break;
                default:
                    break;
            }
        }
    }

    private void upDataOrder() {
        if (nowList == null) {
            nowList = new ArrayList<>();
        }
        final long currtime = System.currentTimeMillis() / 1000;
        lvOrder.setAdapter(new CommonAdapter<NowOrderBean>(context, nowList, R.layout.cell_now_order_list) {
            @Override
            public void convert(ViewHolder helper, NowOrderBean item, int position) {
                helper.setImageByUrl(R.id.img_icon, item.getImg_url());
                helper.setText(R.id.tv_order_sn ,"订单号："+item.getOrder_sn());
                long a = OtherUtils.date2TimeStamp(item.getSended_time())-currtime;
                if (a>0){
                    helper.setText(R.id.tv_orderTimes,"倒计时："+OtherUtils.setShowCountDownText2((int)a));
                }else {
                    helper.setText(R.id.tv_orderTimes,"超时："+OtherUtils.setShowCountDownText2(-(int)a));
                }
                ImageView img_last_time = helper.getView(R.id.img_last_time);
                if (OtherUtils.date2TimeStamp(item.getSended_time()) < currtime) {
                    img_last_time.setVisibility(View.VISIBLE);
                } else {
                    img_last_time.setVisibility(View.GONE);
                }

                if (item.getOrder_type()!=null){
                    if (item.getOrder_type().equals("1")){
                        helper.setText(R.id.tv_order_where,"美团");
                    }else if (item.getOrder_type().equals("2")){
                        helper.setText(R.id.tv_order_where,"饿了么");
                    }else if (item.getOrder_type().equals("3")){
                        helper.setText(R.id.tv_order_where,"百度外卖");
                    }else if (item.getOrder_type().equals("4")){
                        helper.setText(R.id.tv_order_where,"口碑");
                    }
                }else {
                    helper.setText(R.id.tv_order_where,"");
                }
            }
        });
        lvOrder.setEmptyView(emptyView);
        lvOrder.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lvOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                nowList.clear();
                getNowOrder();

            }
        });

        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NowOrderDetailActivity.startAction(context, nowList.get(position - 1));
            }
        });
    }


}
