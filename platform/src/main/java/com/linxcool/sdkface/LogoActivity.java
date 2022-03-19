package com.linxcool.sdkface;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.linxcool.sdkface.feature.YmnProperties;
import com.linxcool.sdkface.util.Logger;
import com.linxcool.sdkface.util.SystemUtil;

/**
 * 闪屏页Activity，包含以下配置：<pre>
 *     res/drawable
 *          third_logo_land.jpg、third_logo_land1.jpg、third_logo_land2.jpg...
 *          third_logo_port.jpg、third_logo_port1.jpg、third_logo_port2.jpg...
 *     assets/ymn.cfg
 *          ymnLogoFrameDuration、ymnLogoFadeDuration
 * </pre>
 *
 * @author 胡昌海(linxcool.hu)
 */
public class LogoActivity extends Activity implements Handler.Callback {

    int DURATION_FRAME = 1000;
    int DURATION_FADE = 200;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);

        //YmnSdk.innerInit(this);
        DURATION_FRAME = getDuration("ymnLogoFrameDuration", DURATION_FRAME);
        DURATION_FADE = getDuration("ymnLogoFadeDuration", DURATION_FADE);

        FrameView view = new FrameView(this);
        setContentView(view);
        view.start();
    }

    private int getDuration(String key, int defVal) {
        String value = YmnProperties.getValue(key);
        if (TextUtils.isEmpty(value)) return defVal;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defVal;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        try {
            startMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void startMainActivity() {
        if (isFinishing()) return;
        ComponentName component = new ComponentName(this, AppConfig.getMainActivity());
        Intent intent = new Intent();
        intent.setComponent(component);
        startActivity(intent);
        finish();
    }

    public class FrameView extends ImageView {

        Context context;
        AnimationDrawable animationDrawable;

        public FrameView(Context context) {
            super(context);
            this.context = context;
            this.animationDrawable = new AnimationDrawable();
            initAnimationDrawable();
            setImageDrawable(animationDrawable);
            setScaleType(ScaleType.CENTER_CROP);
        }

        private void initAnimationDrawable() {
            int logoCount = 0;
            String splashName = "third_logo_land";
            if(!SystemUtil.isScreenLandscape(LogoActivity.this))
                splashName = "third_logo_port";
            for (int i = 0; i < 5; i++) {
                int resId = getDrawableId(splashName + (i == 0 ? "" : i));
                if (resId > 0) {
                    Drawable drawable = context.getResources().getDrawable(resId);
                    animationDrawable.addFrame(drawable, DURATION_FRAME);
                    logoCount++;
                } else {
                    break;
                }
            }
            animationDrawable.setOneShot(true);
            animationDrawable.setEnterFadeDuration(DURATION_FADE);
            animationDrawable.setExitFadeDuration(DURATION_FADE);
            animationDrawable.setFinishListener(new AnimationFinishListener() {
                @Override
                public void onAnimationChanged(int index, boolean finished) {
                    if (finished) {
                        handler.sendEmptyMessageDelayed(0, DURATION_FRAME);
                    }
                }
            });

            if(logoCount <= 1) {
                Logger.w("splash logo image count = " + logoCount);
                handler.sendEmptyMessageDelayed(0, DURATION_FRAME);
            }
        }

        private int getDrawableId(String name) {
            return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        }

        public void start() {
            animationDrawable.start();
        }

    }


    public interface AnimationFinishListener {
        void onAnimationChanged(int index, boolean finished);
    }

    class AnimationDrawable extends android.graphics.drawable.AnimationDrawable {

        private AnimationFinishListener finishListener;

        public AnimationFinishListener getFinishListener() {
            return finishListener;
        }

        void setFinishListener(AnimationFinishListener finishListener) {
            this.finishListener = finishListener;
        }

        @Override
        public boolean selectDrawable(int index) {
            boolean drawableChanged = super.selectDrawable(index);
            if (drawableChanged && finishListener != null) {
                boolean animationFinished = (index == getNumberOfFrames() - 1);
                finishListener.onAnimationChanged(index, animationFinished);
            }
            return drawableChanged;
        }

    }

}
