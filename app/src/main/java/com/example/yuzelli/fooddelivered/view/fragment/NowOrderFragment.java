package com.example.yuzelli.fooddelivered.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.yuzelli.fooddelivered.utils.SharePreferencesUtil;
import com.example.yuzelli.fooddelivered.utils.ViewHolder;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;
    @BindView(R.id.lv_order)
    PullToRefreshListView lvOrder;

    private Context context;
    private List<NowOrderBean> nowList;
    private NowOrderFragmentHandler handler;

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
        tvCenter.setText("当前任务列表");
        tvRight.setVisibility(View.GONE);
        context = getActivity();
        handler = new NowOrderFragmentHandler();
        getNowOrder();
    }

    @Override
    protected void fillData() {


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
                            nowList = GsonUtils.jsonToArrayList(result, NowOrderBean.class);
                            SharePreferencesUtil.saveObject(context, ConstantsUtils.SP_NOW_ORDER_INFO, nowList);
                            handler.sendEmptyMessage(ConstantsUtils.GET_NOW_ORDER_LIST_GET_DATA);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
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

                    upDataOrder();
                    break;

            }
        }
    }

    private void upDataOrder() {
        lvOrder.setAdapter(new CommonAdapter<NowOrderBean>(context,nowList,R.layout.cell_now_order_list) {
            @Override
            public void convert(ViewHolder helper, NowOrderBean item, int position) {
                helper.setImageByUrl(R.id.img_icon,item.getImg_url());
                helper.setText(R.id.tv_orderTimes,"送达时间："+item.getSended_time());

            }
        });
        lvOrder.setEmptyView(emptyView);
    }


}
