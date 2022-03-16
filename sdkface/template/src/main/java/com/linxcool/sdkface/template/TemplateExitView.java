package com.linxcool.sdkface.template;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huchanghai on 2017/11/6.
 */

public class TemplateExitView extends TemplateUi {

    private LinearLayout root;
    private LinearLayout functions;
    private Button success;
    private Button failed;

    public TemplateExitView(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        // 根布局参数
        root = new LinearLayout(context);
        root.setLayoutParams(TemplateLinearParams.create(-1, -2).gravity(Gravity.CENTER).weight(1).leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF).bottomMargin(SIZE_MARGIN_DEF).topMargin(SIZE_MARGIN_DEF).build());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundDrawable(DRAWABLE_DIALOG_BACKGROUND(context));
        root.setMinimumWidth(SIZE_DIALOG_WIDTH);
        // TextView title
        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER);
        title.setText("退出游戏");
        title.setTextSize(17);
        title.setTextColor(COLOR_TEXT_BLACK);
        root.addView(title, dialogTitleParams().topMargin(dip2px(context, 2)).build());
        // functions
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, height / 2));
        scrollView.setScrollbarFadingEnabled(false);
        root.addView(scrollView);
        // functionList
        functions = new LinearLayout(context);
        functions.setOrientation(LinearLayout.VERTICAL);
        functions.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(functions);

        setFunctionView(context);

        // layoutButton
        LinearLayout layoutButton = new LinearLayout(context);
        layoutButton.setOrientation(LinearLayout.HORIZONTAL);
        layoutButton.setPadding(dip2px(context, 25), dip2px(context, 10), dip2px(context, 25), dip2px(context, 10));
        layoutButton.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(layoutButton);
        // ButtonSuccess
        success = new Button(context);
        success.setLayoutParams(TemplateLinearParams.create(-2, dip2px(context, 36)).weight(1).rightMargin(dip2px(context, 20)).build());
        success.setTextSize(15);
        success.setText("退出");
        success.setTextColor(COLOR_TEXT_WHITE);
        success.setBackgroundDrawable(DRAWABLE_BUTTON_GREEN(context));
        layoutButton.addView(success);
        // ButtonFailed
        failed = new Button(context);
        failed.setLayoutParams(TemplateLinearParams.create(-2, dip2px(context, 36)).weight(1).build());
        failed.setTextSize(15);
        failed.setText("取消");
        failed.setTextColor(COLOR_TEXT_GREEN);
        failed.setBackgroundDrawable(DRAWABLE_BUTTON_WHITE(context));
        layoutButton.addView(failed);
    }

    public void setPositiveListener(View.OnClickListener listener) {
        success.setOnClickListener(listener);
    }

    public void setNegativeListener(View.OnClickListener listener) {
        failed.setOnClickListener(listener);
    }

    public void setFunctionView(Context context) {
        HashMap<String, String> descMap = TemplateData.getFunctionNotes();
        List<Map.Entry<String, Integer>> calledFunctions = new ArrayList<>();
        List<Map.Entry<String, Integer>> uncalledFunctions = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : TemplateData.getFunctionMap().entrySet()) {
            if (entry.getValue() > 0) calledFunctions.add(entry);
            else uncalledFunctions.add(entry);
        }

        if (calledFunctions.size() > 0) {
            addFunctionItem(context, "已调用方法：", COLOR_TEXT_BLACK);
            for (Map.Entry<String, Integer> entry : calledFunctions) {
                addFunctionItem(context, String.format("  %s 被调用%d次", entry.getKey(), entry.getValue()), COLOR_TEXT_GREEN);
            }
        }

        if (uncalledFunctions.size() > 0) {
            addFunctionItem(context, "未调用方法：", COLOR_TEXT_BLACK);
            for (Map.Entry<String, Integer> entry : uncalledFunctions) {
                if (descMap.containsKey(entry.getKey())) {
                    addFunctionItem(context, String.format("  %s %s", entry.getKey(), descMap.get(entry.getKey())), COLOR_TEXT_RED);
                } else {
                    addFunctionItem(context, String.format("  %s 可选方法", entry.getKey()), COLOR_TEXT_GRAY);
                }
            }
        }
    }

    private void addFunctionItem(Context context, String text, int color) {
        TextView message = new TextView(context);
        message.setGravity(Gravity.CENTER_VERTICAL);
        message.setText(text);
        message.setTextSize(15);
        message.setTextColor(color);
        functions.addView(message, TemplateLinearParams.create(-1, -2).gravity(Gravity.CENTER_HORIZONTAL).leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF).bottomMargin(dip2px(context, 5)).build());
    }

    @Override
    public View build() {
        return root;
    }
}
