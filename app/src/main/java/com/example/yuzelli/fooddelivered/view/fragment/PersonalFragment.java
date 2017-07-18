package com.example.yuzelli.fooddelivered.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.example.yuzelli.fooddelivered.bean.UserOrderInfo;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.https.OkHttpClientManager;
import com.example.yuzelli.fooddelivered.utils.ActivityCollectorUtil;
import com.example.yuzelli.fooddelivered.utils.CommonAdapter;
import com.example.yuzelli.fooddelivered.utils.GsonUtils;
import com.example.yuzelli.fooddelivered.utils.OtherUtils;
import com.example.yuzelli.fooddelivered.utils.SharePreferencesUtil;
import com.example.yuzelli.fooddelivered.utils.ViewHolder;
import com.example.yuzelli.fooddelivered.view.activity.LoginActivity;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
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
    @BindView(R.id.tv_yue_order_num)
    TextView tv_yue_order_num;
    @BindView(R.id.tv_all_order_num)
    TextView tv_all_order_num;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;

    private Context context;
    private PersionFragmentHandler handler;
    private ArrayList<HistoryOrderBean> orderList;
    public static boolean isNeedGetNewOrderListData = false;
    private int page = 0 ;

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
        orderList = new ArrayList<>();
        handler = new PersionFragmentHandler();
        getUserOrderList();
        getUserInfo();

    }

    private void getUserOrderList() {

        UserInfo user = (UserInfo) SharePreferencesUtil.readObject(context, ConstantsUtils.SP_LOGIN_USER_INFO);
        Map<String, String> map = new HashMap<>();
        map.put("stId", user.getStId());
        map.put("mobile", user.getMobile());
        map.put("page", page+"");
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.GET_HISTORY_ORDER_LIST, map, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        lvOrder.onRefreshComplete();
                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        lvOrder.onRefreshComplete();
                        if (result.substring(0,1).equals("{")){
                            page--;
                            showToast("没有更多了");
                        }else {
                            isNeedGetNewOrderListData = false;
                            orderList.addAll(GsonUtils.jsonToArrayList(result, HistoryOrderBean.class));
                            handler.sendEmptyMessage(ConstantsUtils.GET_HISTORY_ORDER_DATA);
                        }

                    }
                }
        );
    }

    @Override
    protected void fillData() {
      if (isNeedGetNewOrderListData){
          getUserOrderList();
      }
        getUserInfo();
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
                SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_NOW_ORDER_INFO, null);
                JPushInterface.setAlias(context, "", new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {

                    }
                });
                ActivityCollectorUtil.finishAll();
                LoginActivity.startAction(context);

            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();

    }

    public void getUserInfo() {

        UserInfo user = (UserInfo) SharePreferencesUtil.readObject(context, ConstantsUtils.SP_LOGIN_USER_INFO);
        final Map<String, String> map = new HashMap<>();
        map.put("stId", user.getStId());
        map.put("mobile", user.getMobile());
        OkHttpClientManager.postAsync(ConstantsUtils.ADDRESS_URL + ConstantsUtils.GET_HISTORY_UHSER_COUNT_INFO, map, new OkHttpClientManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        showToast("获取数据失败！");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {

                           if (result.substring(0,1).equals("{")){
                               Gson gson = new Gson();
                               UserOrderInfo uoi = gson.fromJson(result,UserOrderInfo.class);
                               Message msg = new Message();
                               msg.obj = uoi;
                               msg.what = ConstantsUtils.GET_HISTORY_COOUNT_DATA;
                               handler.sendMessage(msg);
                           }else {
                               showToast("数据获取失败！");
                           }
                  

                      

                    }
                }
        );
    }


    class PersionFragmentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.GET_HISTORY_ORDER_DATA:
                    updataListView();
                    break;    
                case ConstantsUtils.GET_HISTORY_COOUNT_DATA:
                    UserOrderInfo uoInfo = (UserOrderInfo) msg.obj;
                    updataView(uoInfo);
                    break;
                default:
                    break;
            }

        }
    }

    private void updataView( UserOrderInfo uoInfo) {
        tv_yue_order_num.setText("本月订单："+uoInfo.getMall()+"单      超时："+uoInfo.getMouttime()+"单");
       tv_all_order_num.setText("所有订单："+uoInfo.getAorder()+"单      超时："+uoInfo.getAouttime()+"单");
    }

    private void updataListView() {
        lvOrder.setMode(PullToRefreshBase.Mode.BOTH);

        if (orderList==null){
            orderList = new ArrayList<>();
        }
        lvOrder.setAdapter(new CommonAdapter<HistoryOrderBean>(context, orderList, R.layout.cell_history_order) {
            @Override
            public void convert(ViewHolder helper, HistoryOrderBean item, int position) {
                helper.setText(R.id.tv_addtime, "创单时间：" + item.getAdd_time());
                helper.setText(R.id.tv_confirmTime, "接单时间：" + item.getConfirm_time());
                helper.setText(R.id.tv_finishTime, "完成时间：" + item.getFinished_time());
                helper.setText(R.id.tv_sendTime, "送达时间：" + item.getSended_time());
                ImageView img_last_time = helper.getView(R.id.img_last_time);

                switch (Integer.valueOf(item.getOrder_status())) {
                    case 1:
                        helper.setText(R.id.tv_state, "进行中");
                        img_last_time.setVisibility(View.GONE);
                        break;
                    case 2:
                        helper.setText(R.id.tv_state, "已完成");
                        img_last_time.setVisibility(View.GONE);
                        break;
                    case 3:
                        helper.setText(R.id.tv_state, "超时");
                        img_last_time.setVisibility(View.VISIBLE);
                        break;
                }

                if (item.getOrder_type()!=null){
                    if (item.getOrder_type().equals("1")){
                        helper.setText(R.id.tv_order_where,"订单平台：美团");
                    }else if (item.getOrder_type().equals("2")){
                        helper.setText(R.id.tv_order_where,"订单平台：饿了么");
                    }else if (item.getOrder_type().equals("3")){
                        helper.setText(R.id.tv_order_where,"订单平台：百度外卖");
                    }else if (item.getOrder_type().equals("4")){
                        helper.setText(R.id.tv_order_where,"订单平台：口碑");
                    }
                }else {
                    helper.setText(R.id.tv_order_where,"");
                }

            }
        });
        lvOrder.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 0;
                orderList.clear();
                getUserOrderList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page ++;
                getUserOrderList();
            }

        });
        lvOrder.setEmptyView(emptyView);
    }
}
