package com.example.yuzelli.fooddelivered.view.fragment;

import android.content.Context;
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
import com.example.yuzelli.fooddelivered.utils.ViewHolder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;

import java.io.IOException;
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

    private String lastResult;


    @Override
    protected int layoutInit() {
        return R.layout.fragment_order;
    }

    @Override
    protected void bindEvent(View v) {
        ivLeft.setVisibility(View.GONE);
        tvRight.setVisibility(View.GONE);
        tvCenter.setText("新订单！");
        tvHint.setText("暂无新的订单");
        handler = new OrderFragmentHandler();
        context = getActivity();
        lvOrder.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        doGetOrderList();
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
                            return;
                        } else {
                            lastResult = result;
                            orderListDatas = OrderBean.getOrderList(result);
                            handler.sendEmptyMessage(ConstantsUtils.NEW_ORDER_LIST_GET_DATA);
                        }
                    }
                }
        );
    }

    @Override
    protected void fillData() {

    }


    class OrderFragmentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.NEW_ORDER_LIST_GET_DATA:
                    updataListView();
                    break;
                default:
                    break;
            }
        }
    }

    private void updataListView() {
        lvOrder.setEmptyView(emptyView);
        lvOrder.setAdapter(new CommonAdapter<OrderBean>(context,orderListDatas,R.layout.cell_order_item) {
            @Override
            public void convert(ViewHolder helper, OrderBean item, int position) {
                helper.setText(R.id.tv_userName,item.getUsername());
                helper.setText(R.id.tv_userAddress,item.getAddress());
            }
        });
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderListDatas.get(position+1);
            }
        });
        lvOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetOrderList();
            }
        });
    }

}
