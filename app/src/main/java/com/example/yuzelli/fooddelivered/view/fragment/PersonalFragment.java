package com.example.yuzelli.fooddelivered.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseFragment;
import com.example.yuzelli.fooddelivered.bean.HistoryOrderBean;
import com.example.yuzelli.fooddelivered.bean.NowOrderBean;
import com.example.yuzelli.fooddelivered.bean.UserInfo;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.ActivityCollectorUtil;
import com.example.yuzelli.fooddelivered.utils.CommonAdapter;
import com.example.yuzelli.fooddelivered.utils.GsonUtils;
import com.example.yuzelli.fooddelivered.utils.SharePreferencesUtil;
import com.example.yuzelli.fooddelivered.utils.ViewHolder;
import com.example.yuzelli.fooddelivered.view.activity.LoginActivity;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by 51644 on 2017/7/6.
 */

public class PersonalFragment extends BaseFragment {
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_center)
    TextView tvCenter;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.lv_order)
    PullToRefreshListView lvOrder;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;

    private Context context;
    private PersionFragmentHandler handler;
    private ArrayList<HistoryOrderBean> orderList;

    @Override
    protected int layoutInit() {
        return R.layout.fragment_personal;
    }

    @Override
    protected void bindEvent(View v) {
        ivLeft.setVisibility(View.GONE);
        tvCenter.setText("个人中心");
        tvRight.setText("退出");
        context = getActivity();
        handler = new PersionFragmentHandler();
        getUserOrderList();
    }

    private void getUserOrderList() {
        UserInfo user = (UserInfo) SharePreferencesUtil.readObject(context, ConstantsUtils.SP_LOGIN_USER_INFO);
        Map<String, String> map = new HashMap<>();
        map.put("stId", user.getStId());
        map.put("mobile", user.getMobile());
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.GET_HISTORY_ORDER_LIST, map, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        lvOrder.onRefreshComplete();
                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        lvOrder.onRefreshComplete();
                        orderList = GsonUtils.jsonToArrayList(result, HistoryOrderBean.class);
                        handler.sendEmptyMessage(ConstantsUtils.GET_HISTORY_ORDER_DATA);
                    }
                }
        );
    }

    @Override
    protected void fillData() {

    }

    @OnClick(R.id.tv_right)
    public void onViewClicked() {
        showExitLogin();
    }

    private void showExitLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("你确定退出当前用户");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_LOGIN_USER_INFO, null);
                ActivityCollectorUtil.finishAll();
                LoginActivity.startAction(context);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();

    }


    class PersionFragmentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.GET_HISTORY_ORDER_DATA:
                    updataListView();
                    break;
                default:
                    break;
            }

        }
    }

    private void updataListView() {
        lvOrder.setAdapter(new CommonAdapter<HistoryOrderBean>(context, orderList, R.layout.cell_history_order) {
            @Override
            public void convert(ViewHolder helper, HistoryOrderBean item, int position) {
                helper.setText(R.id.tv_addtime, "创单时间：" + item.getAdd_time());
                helper.setText(R.id.tv_confirmTime, "接单时间：" + item.getConfirm_time());
                helper.setText(R.id.tv_finishTime, "完成时间：" + item.getFinished_time());
                switch (Integer.valueOf(item.getOrder_status())) {
                    case 1:
                        helper.setText(R.id.tv_state, "进行中");
                        break;
                    case 2:
                        helper.setText(R.id.tv_state, "已完成");
                        break;
                }

            }
        });
        lvOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getUserOrderList();
            }
        });
        lvOrder.setEmptyView(emptyView);
    }
}
