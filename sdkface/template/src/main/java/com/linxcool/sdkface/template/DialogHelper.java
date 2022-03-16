package com.linxcool.sdkface.template;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DialogHelper {

    public static void showLog(Context context, StringBuilder builder) {
        int margin = dip2px(context, 8);
        // 根布局
        ScrollView layoutRoot = new ScrollView(context);
        GradientDrawable drawable_layoutRoot = new GradientDrawable();
        drawable_layoutRoot.setColor(Color.parseColor("#f2f4f4"));
        layoutRoot.setBackgroundDrawable(drawable_layoutRoot);
        layoutRoot.setPadding(margin, margin, margin, margin);
        layoutRoot.setScrollbarFadingEnabled(false);
        layoutRoot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TextView message = new TextView(context);
        message.setGravity(Gravity.CENTER_VERTICAL);
        message.setText(Html.fromHtml(builder.toString()));
        message.setTextSize(13);
        message.setTextColor(Color.parseColor("#666666"));
        layoutRoot.addView(message, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        final Dialog logDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_DialogWhenLarge);
        Window window = logDialog.getWindow();
        GradientDrawable drawable_normaldialog = new GradientDrawable();
        drawable_normaldialog.setColor(Color.TRANSPARENT);
        window.setBackgroundDrawable(drawable_normaldialog);
        logDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logDialog.setContentView(layoutRoot);
        logDialog.setCancelable(true);
        logDialog.setCanceledOnTouchOutside(true);
        logDialog.show();
    }

    /**
     * 根据手机的分辨率从 DP 的单位 转成为 PX(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
