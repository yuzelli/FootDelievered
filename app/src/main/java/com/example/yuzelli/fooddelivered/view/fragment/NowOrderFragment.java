package com.example.yuzelli.fooddelivered.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseFragment;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import com.example.yuzelli.fooddelivered.view.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    private Context context;
    private NowOrderFragmentHandler handler;




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
        current_time = all_time;
        Message message = handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);     // Message
        handler.sendMessageDelayed(message, 1000);
    }

    @Override
    protected void fillData() {

    }

    @OnClick(R.id.btn_finish_order)
    public void onViewClicked() {

    }

    class NowOrderFragmentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.SENG_COUNT_DOWN_MESSAGE:
                    current_time--;
                    tvCountDown.setText("" + setShowCountDownText(current_time));

                    if (current_time > 0) {
                        Message message = handler.obtainMessage(ConstantsUtils.SENG_COUNT_DOWN_MESSAGE);
                        handler.sendMessageDelayed(message, 1000);      // send message
                    } else {
                        tvCountDown.setText("完成！");
                    }
                    break;

            }
        }
    }

  private String  setShowCountDownText(int time){
      StringBuffer buffer = new StringBuffer();
      int feng = time/60;
      int min = time%60;
      return buffer.append("倒计时：").append(feng+"分").append(min+"秒").toString();

  }

}
