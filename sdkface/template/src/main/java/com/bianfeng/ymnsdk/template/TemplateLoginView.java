package com.bianfeng.ymnsdk.template;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by huchanghai on 2017/11/6.
 */

public class TemplateLoginView extends TemplateUi {

    private LinearLayout layout;
    private EditText user;
    private EditText password;
    private Button success;
    private Button failed;
    private Button cancel;

    public TemplateLoginView(Context context) {
        // root
        layout = new LinearLayout(context);
        layout.setLayoutParams(dialogRootParams().build());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundDrawable(DRAWABLE_DIALOG_BACKGROUND(context));
        layout.setMinimumWidth(SIZE_DIALOG_WIDTH);
        layout.setMinimumHeight(SIZE_DIALOG_HEIGHT);
        // title
        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER);
        title.setText("登录测试");
        title.setTextSize(18);
        title.setTextColor(COLOR_TEXT_BLACK);
        layout.addView(title, dialogTitleParams().build());
        // line
        View line = new View(context);
        line.setBackgroundDrawable(DRAWABLE_LINE(context));
        layout.addView(line, new LinearLayout.LayoutParams(-1, dip2px(context, 2)));
        // userName
        user = new EditText(context);
        user.setPadding(dip2px(context, 8), 0, dip2px(context, 8), 0);
        user.setHint("此处输入用户名，可不输");
        user.setSingleLine(true);
        user.setCursorVisible(false);
        user.setTextColor(COLOR_TEXT_GRAY);
        user.setTextSize(14);
        user.setBackgroundDrawable(DRAWABLE_EDIT_BACKGROUND(context));
        layout.addView(user, editParams().build());
        // password
        password = new EditText(context);
        password.setPadding(dip2px(context, 8), 0, dip2px(context, 8), 0);
        password.setHint("此处输入密码，可不输");
        password.setSingleLine(true);
        password.setCursorVisible(false);
        password.setTextColor(COLOR_TEXT_GRAY);
        password.setTextSize(14);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setBackgroundDrawable(DRAWABLE_EDIT_BACKGROUND(context));
        layout.addView(password, editParams().build());
        // layout for buttons
        LinearLayout layoutButton = new LinearLayout(context);
        layoutButton.setOrientation(LinearLayout.HORIZONTAL);
        layoutButton.setPadding(dip2px(context, 25), SIZE_MARGIN_DEF, dip2px(context, 25), 0);
        layoutButton.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(layoutButton);
        // ButtonSuccess
        success = new Button(context);
        success.setLayoutParams(buttonParams().build());
        success.setTextSize(15);
        success.setText("成功");
        success.setTextColor(COLOR_TEXT_WHITE);
        success.setBackgroundDrawable(DRAWABLE_BUTTON_GREEN(context));
        layoutButton.addView(success);
        // ButtonFailed
        failed = new Button(context);
        failed.setLayoutParams(buttonParams().leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF).build());
        failed.setTextSize(15);
        failed.setText("失败");
        failed.setTextColor(COLOR_TEXT_GREEN);
        failed.setBackgroundDrawable(DRAWABLE_BUTTON_WHITE(context));
        layoutButton.addView(failed);
        // ButtonCancel
        cancel = new Button(context);
        cancel.setLayoutParams(buttonParams().build());
        cancel.setTextSize(15);
        cancel.setText("取消");
        cancel.setTextColor(COLOR_TEXT_GREEN);
        cancel.setBackgroundDrawable(DRAWABLE_BUTTON_WHITE(context));
        layoutButton.addView(cancel);

        // TextView notice
        TextView notice = new TextView(context);
        notice.setGravity(Gravity.CENTER);
        notice.setText("注意：进入游戏主界面且toast提示“用户数据已提交”视作成功");
        notice.setTextSize(10);
        notice.setTextColor(COLOR_TEXT_RED);
        layout.addView(notice, TemplateLinearParams.create(-1, dip2px(context, 20)).gravity(Gravity.CENTER).topMargin(dip2px(context, 5)).bottomMargin(dip2px(context, 5)).build());
    }

    public String getUserName() {
        return user.getText().toString();
    }

    public String getPassword() {
        return password.getText().toString();
    }

    public void setOnClickListener(final OnClickListener listener) {
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSuccess(TemplateLoginView.this);
            }
        });
        failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFailure(TemplateLoginView.this);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(TemplateLoginView.this);
            }
        });
    }

    @Override
    public View build() {
        return layout;
    }

}
