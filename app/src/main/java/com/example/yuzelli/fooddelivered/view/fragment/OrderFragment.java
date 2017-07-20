package com.example.yuzelli.fooddelivered.view.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
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
import com.example.yuzelli.fooddelivered.bean.OrderBean;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.CommonAdapter;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;
import com.example.yuzelli.fooddelivered.utils.ViewHolder;
import com.example.yuzelli.fooddelivered.view.activity.OrderDetailActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by 51644 on 2017/7/6.
 * 订单主页面
 */

public class OrderFragment extends BaseFragment {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.lv_order)
    PullToRefreshListView lvOrder;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;

    private List<OrderBean> orderListDatas;
    private OrderFragmentHandler handler;
    private Context context;

    private static String lastResult;
    private SoundPool soundPool;

    public static boolean needGETOrder = false;

    @Override
    protected int layoutInit() {
        return R.layout.fragment_order;
    }

    @Override
    protected void bindEvent(View v) {
        ivLeft.setVisibility(View.GONE);
        tvRight.setVisibility(View.GONE);
        tvCenter.setText("订单列表");
        tvHint.setText("暂无新的订单");
        handler = new OrderFragmentHandler();
        context = getActivity();
        lvOrder.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        doGetOrderList();
        soundPool  = new SoundPool(21, AudioManager.STREAM_SYSTEM,10);
        soundPool.load(context,R.raw.test,1);

        Message message = handler.obtainMessage(ConstantsUtils.SENG_GET_ORDER_MESSAGE);     // Message
        handler.sendMessageDelayed(message, 30*1000);
    }
    private void doGetOrderList() {

        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.NEW_ORDER_LIST, null, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        lvOrder.onRefreshComplete();
                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        lvOrder.onRefreshComplete();

                        String first = result.substring(0, 1);
                        if (first.equals("{")) {
                            if (orderListDatas!=null){
                                orderListDatas.clear();
                            }

                            updataListView();
                            return;
                        } else {
                            if (result.equals(lastResult)){

                                return;
                            }
                            showHintSound();
                            lastResult = result;
                            orderListDatas = OrderBean.getOrderList(result);
                            handler.sendEmptyMessage(ConstantsUtils.NEW_ORDER_LIST_GET_DATA);

                        }
                    }
                }
        );
    }

    private void showHintSound() {

        soundPool.play(1,1, 1, 0, 0,  1f);
    }

    @Override
    protected void fillData() {
      //  handler = new OrderFragmentHandler();
//        Message message = handler.obtainMessage(ConstantsUtils.SENG_GET_ORDER_MESSAGE);     // Message
//        handler.sendMessageDelayed(message, 5*1000);
        if (needGETOrder){
            doGetOrderList();
            needGETOrder = false;
        }
    }


    class OrderFragmentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.NEW_ORDER_LIST_GET_DATA:
                    updataListView();
                    break;
                case ConstantsUtils.SENG_GET_ORDER_MESSAGE:
                    doGetOrderList();
                    Message message = handler.obtainMessage(ConstantsUtils.SENG_GET_ORDER_MESSAGE);
                    handler.sendMessageDelayed(message, 30*1000);
                    break;

                default:
                    break;
            }
        }
    }


    private void updataListView() {
        lvOrder.setEmptyView(emptyView);
        if (  orderListDatas==null){
            orderListDatas = new ArrayList<>();
        }
        CommonAdapter<OrderBean> adapter = new CommonAdapter<OrderBean>(context,orderListDatas,R.layout.cell_now_order_list) {
            @Override
            public void convert(ViewHolder helper, OrderBean item, int position) {
                helper.setImageByUrl(R.id.img_icon,item.getImg_url());
                long a  = OtherUtils.date2TimeStamp(item.getAdd_time())+Integer.valueOf(item.getOuttime());
                helper.setText(R.id.tv_orderTimes,"送达时间："+OtherUtils.stampToDate(a+""));
                helper.setText(R.id.tv_order_sn,"订单号："+item.getOrder_sn());
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
        };
        lvOrder.setAdapter(adapter);

        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderDetailActivity.actionStart(context, orderListDatas.get(position-1));
            }
        });



        lvOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                lastResult = "";
                doGetOrderList();

            }
        });


    }
    private String setShowCountDownText(int time) {
        StringBuffer buffer = new StringBuffer();
        int feng = time / 60;
        int min = time % 60;
        return buffer.append(feng + "分").append(min + "秒").toString();
    }
}
