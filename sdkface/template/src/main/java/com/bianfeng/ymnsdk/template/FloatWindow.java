package com.bianfeng.ymnsdk.template;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bianfeng.ymnsdk.YmnSdk;
import com.bianfeng.ymnsdk.feature.protocol.IUserFeature;
import com.bianfeng.ymnsdk.util.Logger;

public class FloatWindow extends TemplateUi {

    private WindowManager windowManager;
    private LayoutParams windowParams;

    private LinearLayout floatContainer;
    private LinearLayout floatDetail;
    private Button floatIcon;

    private float touchStartX;
    private float touchStartY;
    private float x;
    private float y;

    private int itemSize;

    public FloatWindow(Context context) {
        initWindow(context);
        initView(context);
    }

    private void initWindow(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        windowParams = new LayoutParams();
        windowParams.type = LayoutParams.TYPE_APPLICATION_PANEL;
        windowParams.format = PixelFormat.RGBA_8888;
        windowParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        windowParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = windowManager.getDefaultDisplay().getHeight() * 2 / 3;
        windowParams.width = LayoutParams.WRAP_CONTENT;
        windowParams.height = LayoutParams.WRAP_CONTENT;
    }

    private void initView(final Context context) {
        itemSize = dip2px(context, 48);

        // 根布局
        floatContainer = new LinearLayout(context);
        floatContainer.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        floatContainer.setOrientation(LinearLayout.HORIZONTAL);
        floatContainer.setBackgroundColor(Color.TRANSPARENT);
        windowManager.addView(floatContainer, windowParams);

        // Button Float
        floatIcon = createItem(context, "悬浮框");
        floatContainer.addView(floatIcon, TemplateLinearParams.create(-2, -2).weight(4).build());

        floatDetail = createFloatDetail(context);
        floatContainer.addView(floatDetail, TemplateLinearParams.create(-2, -2).weight(1).build());

        // 设置监听浮动窗口的触摸移动
        floatIcon.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (floatDetail.getVisibility() == View.VISIBLE) return false;

                x = event.getRawX();
                y = event.getRawY();// - dip2px(context, 25);
                Log.i("currP", "currX" + x + "====currY" + y);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchStartX = event.getX();
                        touchStartY = event.getY();
                        Log.i("startP", "startX" + touchStartX + "====startY" + touchStartY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        touchStartX = touchStartY = 0;
                        break;
                }
                return false;
            }
        });

        floatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visible = floatDetail.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                floatDetail.setVisibility(visible);
            }
        });
    }

    private LinearLayout createFloatDetail(final Context context) {
        final LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundColor(Color.parseColor("#00000000"));
        layout.setVisibility(View.GONE);

        // 点击其他地方消失
        layout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (layout.getVisibility() == View.VISIBLE) layout.setVisibility(View.GONE);
                return false;
            }
        });

        // 切换账号
        addToLayout(layout, createItem(context, "切换\n账号")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                YmnSdk.callFunction(IUserFeature.FUNCTION_ACCOUNT_SWITCH);
            }
        });
        // 登出账号
        addToLayout(layout, createItem(context, "注销\n账号")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                YmnSdk.callFunction(IUserFeature.FUNCTION_LOGOUT);
            }
        });
        // 修改密码
        addToLayout(layout, createItem(context, "修改\n密码")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                YmnSdk.callFunction("resetPassword");
            }
        });
        // 查看日志，该功能暂不外放
        addToLayout(layout, createItem(context, "查看\n日志")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                DialogHelper.showLog(context, Logger.getCacheLog());
            }
        });

        return layout;
    }

    private Button createItem(Context context, String text) {
        Button button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        button.setWidth(itemSize);
        button.setHeight(itemSize);
        button.setTextSize(14);
        button.setText(text);
        button.setPadding(0, 0, 0, 0);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.parseColor("#6EB2A3"));
        return button;
    }

    private Button addToLayout(ViewGroup group, Button button) {
        group.addView(button);
        return button;
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        windowParams.x = (int) (x - touchStartX);
        windowParams.y = (int) (y - touchStartY);
        windowManager.updateViewLayout(floatContainer, windowParams);
    }

    @Override
    public View build() {
        return null;
    }

    public synchronized void show() {
        try {
            Logger.i("show float " + floatContainer);
            floatContainer.setVisibility(View.VISIBLE);
            windowManager.updateViewLayout(floatContainer, windowParams);
        } catch (Exception e) {
            e.printStackTrace();
            floatContainer = null;
        }
    }

    public void release() {
        if (floatContainer != null) {
            Logger.i("release float " + floatContainer);
            windowManager.removeView(floatContainer);
            floatContainer = null;
            windowParams = null;
            windowManager = null;
        }
    }

}
