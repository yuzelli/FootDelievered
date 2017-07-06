package com.example.yuzelli.fooddelivered.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ImageView;
import com.example.yuzelli.fooddelivered.R;
import com.example.yuzelli.fooddelivered.base.BaseActivity;
import com.example.yuzelli.fooddelivered.constants.ConstantsUtils;
import java.util.Random;

/**
 * 闪屏页，判断用户是否第一次登录
 * @author 李秉龙
 */
public class SplashActivity extends BaseActivity {
    private Context context;
    private boolean nextLogin = false;
    private ImageView iv_spl_background;
    private static final int ANIMATION_DURATION = 3000;
    private static final float SCALE_END = 1.13F;
    private static final int[] SPLASH_ARRAY = {
            R.drawable.splash3,
            R.drawable.splash7,
            R.drawable.splash10,
            R.drawable.splash16,
    };

    @Override
    protected int layoutInit() {
        return R.layout.activity_splash;
    }

    @Override
    protected void binEvent() {
        iv_spl_background = (ImageView) this.findViewById(R.id.iv_spl_background);
        Random r = new Random(SystemClock.elapsedRealtime());
        iv_spl_background.setImageResource(SPLASH_ARRAY[r.nextInt(SPLASH_ARRAY.length)]);
        context = SplashActivity.this;
        animateImage();

    }

    @Override
    protected void fillData() {

    }


    private void animateImage() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(iv_spl_background, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(iv_spl_background, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // handler.sendEmptyMessageDelayed(ConstantUtil.START_ACTIVITY, 3000);
                handler.sendEmptyMessage(ConstantsUtils.SPLASH_START_ACTIVITY);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantsUtils.SPLASH_START_ACTIVITY:

                    break;
                case ConstantsUtils.LOGIN_GET_DATA:

                    break;
                default:
                    break;
            }
        }
    };


}