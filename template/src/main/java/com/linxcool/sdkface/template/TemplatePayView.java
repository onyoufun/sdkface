package com.linxcool.sdkface.template;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by huchanghai on 2017/11/6.
 */

public class TemplatePayView extends TemplateUi {

    private LinearLayout layout;
    private TextView noParamsText;
    private TextView hasParamsText;
    private Button success;
    private Button failed;
    private Button cancel;

    public TemplatePayView(Context context) {
        // root
        layout = new LinearLayout(context);
        layout.setLayoutParams(dialogRootParams().build());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundDrawable(DRAWABLE_DIALOG_BACKGROUND(context));
        layout.setMinimumWidth(SIZE_DIALOG_WIDTH);
        // TextView title
        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER);
        title.setText("支付测试");
        title.setTextSize(18);
        title.setTextColor(COLOR_TEXT_BLACK);
        layout.addView(title, dialogTitleParams().build());
        // View line
        View line = new View(context);
        line.setBackgroundDrawable(DRAWABLE_LINE(context));
        layout.addView(line, new LinearLayout.LayoutParams(-1, dip2px(context, 2)));
        // 参数ScrollView
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, SIZE_DIALOG_HEIGHT * 2 / 3));
        scrollView.setScrollbarFadingEnabled(false);
        layout.addView(scrollView);

        LinearLayout paramaLinearLayout = new LinearLayout(context);
        paramaLinearLayout.setOrientation(LinearLayout.VERTICAL);
        paramaLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        scrollView.addView(paramaLinearLayout);

        // TextView 显示未传入参数
        noParamsText = new TextView(context);
        noParamsText.setTextColor(COLOR_TEXT_RED);
        noParamsText.setTextSize(14);
        paramaLinearLayout.addView(noParamsText, TemplateLinearParams.create(-1, -2).topMargin(dip2px(context, 8)).leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF).build());
        // TextView 显示传入参数
        hasParamsText = new TextView(context);
        hasParamsText.setTextColor(COLOR_TEXT_GREEN);
        hasParamsText.setTextSize(14);
        paramaLinearLayout.addView(hasParamsText, TemplateLinearParams.create(-1, -2).topMargin(dip2px(context, 5)).leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF).build());
        // TextView notice
        TextView notice = new TextView(context);
        notice.setText("提示：母包支付成功后，有猫腻通知CP服务端的pay_status为0（正式打包后为1），请测试时作为支付成功处理。");
        notice.setTextSize(13);
        notice.setTextColor(COLOR_TEXT_RED);
        paramaLinearLayout.addView(notice, TemplateLinearParams.create(-1, -2).topMargin(dip2px(context, 5)).leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF).build());
        // message
        TextView orderresult = new TextView(context);
        orderresult.setText("请选择你想要的支付测试结果!");
        orderresult.setTextColor(COLOR_TEXT_GRAY);
        orderresult.setTextSize(14);
        layout.addView(orderresult, TemplateLinearParams.create(-1, -2).topMargin(dip2px(context, 8)).leftMargin(dip2px(context, 25)).build());
        // layoutButton
        LinearLayout layoutButton = new LinearLayout(context);
        layoutButton.setOrientation(LinearLayout.HORIZONTAL);
        layoutButton.setPadding(dip2px(context, 25), dip2px(context, 8), dip2px(context, 25), dip2px(context, 10));
        layoutButton.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(layoutButton);
        // ButtonSuccess
        success = new Button(context);
        success.setLayoutParams(TemplateLinearParams.create(-2, dip2px(context, 38)).weight(1).rightMargin(dip2px(context, 15)).build());
        success.setTextSize(15);
        success.setText("成功");
        success.setTextColor(COLOR_TEXT_WHITE);
        success.setBackgroundDrawable(DRAWABLE_BUTTON_GREEN(context));
        layoutButton.addView(success);
        // ButtonFailed
        failed = new Button(context);
        failed.setLayoutParams(TemplateLinearParams.create(-2, dip2px(context, 38)).weight(1).build());
        failed.setTextSize(15);
        failed.setText("失败");
        failed.setTextColor(COLOR_TEXT_GREEN);
        failed.setBackgroundDrawable(DRAWABLE_BUTTON_WHITE(context));
        layoutButton.addView(failed);
        // ButtonCancel
        cancel = new Button(context);
        cancel.setLayoutParams(TemplateLinearParams.create(-2, dip2px(context, 38)).weight(1).leftMargin(dip2px(context, 15)).build());
        cancel.setTextSize(15);
        cancel.setText("取消");
        cancel.setTextColor(COLOR_TEXT_GREEN);
        cancel.setBackgroundDrawable(DRAWABLE_BUTTON_WHITE(context));
        layoutButton.addView(cancel);
    }

    public void setOfferedParamsText(CharSequence text) {
        hasParamsText.setText(text);
    }

    public void setLackedParamsText(CharSequence text) {
        noParamsText.setText(text);
    }

    public void setOnClickListener(final OnClickListener listener) {
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSuccess(TemplatePayView.this);
            }
        });
        failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFailure(TemplatePayView.this);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(TemplatePayView.this);
            }
        });
    }

    @Override
    public View build() {
        return layout;
    }
}
