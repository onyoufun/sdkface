package com.bianfeng.ymnsdk.template;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by huchanghai on 2017/11/6.
 */

public class TemplateConfirmView extends TemplateUi {

    private LinearLayout layout;
    private TextView title;
    private TextView message;
    private Button success;
    private Button failed;

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setMessage(String text) {
        message.setText(Html.fromHtml(text));
    }

    public void setPositive(String text, View.OnClickListener listener) {
        success.setText(text);
        success.setOnClickListener(listener);
    }

    public void setNegative(String text, View.OnClickListener listener) {
        failed.setText(text);
        failed.setOnClickListener(listener);
    }

    public TemplateConfirmView(Context context) {
        // 根布局
        layout = new LinearLayout(context);
        layout.setLayoutParams(dialogRootParams().build());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundDrawable(DRAWABLE_DIALOG_BACKGROUND(context));
        layout.setMinimumWidth(SIZE_DIALOG_WIDTH);
        layout.setMinimumHeight(dip2px(context, 160));
        // TextView title
        title = new TextView(context);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(17);
        title.setTextColor(COLOR_TEXT_BLACK);
        layout.addView(title, dialogTitleParams().build());
        // TextView message
        message = new TextView(context);
        message.setGravity(Gravity.CENTER_VERTICAL);
        message.setTextSize(13);
        message.setTextColor(Color.parseColor("#666666"));
        layout.addView(message, TemplateLinearParams.create(-1, dip2px(context, 60)).gravity(Gravity.CENTER_HORIZONTAL).leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF).build());
        // layoutButton
        LinearLayout layoutButton = new LinearLayout(context);
        layoutButton.setOrientation(LinearLayout.HORIZONTAL);
        layoutButton.setPadding(dip2px(context, 25), dip2px(context, 10), dip2px(context, 25), dip2px(context, 10));
        layoutButton.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(layoutButton);
        // ButtonSuccess
        success = new Button(context);
        success.setLayoutParams(TemplateLinearParams.create(-2, dip2px(context, 36)).weight(1).rightMargin(dip2px(context, 20)).build());
        success.setTextSize(15);
        success.setTextColor(COLOR_TEXT_WHITE);
        success.setBackgroundDrawable(DRAWABLE_BUTTON_GREEN(context));
        layoutButton.addView(success);
        // ButtonFailed
        failed = new Button(context);
        failed.setLayoutParams(TemplateLinearParams.create(-2, dip2px(context, 36)).weight(1).build());
        failed.setTextSize(15);
        failed.setTextColor(COLOR_TEXT_GREEN);
        failed.setBackgroundDrawable(DRAWABLE_BUTTON_WHITE(context));
        layoutButton.addView(failed);
    }

    @Override
    public View build() {
        return layout;
    }
}
