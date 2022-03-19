package com.linxcool.sdkface.template;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

/**
 * Created by huchanghai on 2017/11/6.
 */

public abstract class TemplateUi {

    static int SIZE_MARGIN_DEF;
    static int SIZE_DIALOG_WIDTH;
    static int SIZE_DIALOG_HEIGHT;
    static int SIZE_DIALOG_TITLE;
    static int SIZE_EDIT_WIDTH;
    static int SIZE_EDIT_HEIGHT;

    static final int COLOR_DIALOG_BACKGROUND = Color.parseColor("#f2f4f4");
    static final int COLOR_TEXT_BLACK = Color.parseColor("#334550");
    static final int COLOR_TEXT_GRAY = Color.parseColor("#b0b5b9");
    static final int COLOR_TEXT_WHITE = Color.parseColor("#ffffff");
    static final int COLOR_TEXT_GREEN = Color.parseColor("#6eb23f");
    static final int COLOR_TEXT_RED = Color.parseColor("#ff0000");
    static final int COLOR_LINE = Color.parseColor("#b0b5b9");

    private static Dialog dialog;

    public abstract View build();

    public static void init(Context context) {
        SIZE_MARGIN_DEF = dip2px(context, 15);
        SIZE_DIALOG_WIDTH = dip2px(context, 330);
        SIZE_DIALOG_HEIGHT = dip2px(context, 245);
        SIZE_DIALOG_TITLE = dip2px(context, 40);
        SIZE_EDIT_WIDTH = dip2px(context, 280);
        SIZE_EDIT_HEIGHT = dip2px(context, 38);
    }

    public void show(Activity activity) {
        View view = build();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(DRAWABLE_TRANSPARENT());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static GradientDrawable DRAWABLE_TRANSPARENT() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        return drawable;
    }

    public static GradientDrawable DRAWABLE_DIALOG_BACKGROUND(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(COLOR_DIALOG_BACKGROUND);
        drawable.setCornerRadius(dip2px(context, 4));
        return drawable;
    }

    public static GradientDrawable DRAWABLE_LINE(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.LINE);
        drawable.setStroke(dip2px(context, 1), COLOR_LINE, dip2px(context, 4), dip2px(context, 3));
        return drawable;
    }

    public static GradientDrawable DRAWABLE_EDIT_BACKGROUND(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(dip2px(context, 1), Color.parseColor("#c5c9cc"));
        drawable.setColor(Color.parseColor("#e8eae9"));
        drawable.setCornerRadius(dip2px(context, 4));
        return drawable;
    }

    public static GradientDrawable DRAWABLE_BUTTON_GREEN(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(dip2px(context, 1), Color.parseColor("#6eb23f"));
        drawable.setColor(Color.parseColor("#6eb23f"));
        drawable.setCornerRadius(dip2px(context, 4));
        return drawable;
    }

    public static GradientDrawable DRAWABLE_BUTTON_WHITE(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(dip2px(context, 1), Color.parseColor("#6eb23f"));
        drawable.setColor(Color.parseColor("#f2f4f4"));
        drawable.setCornerRadius(dip2px(context, 4));
        return drawable;
    }

    public static TemplateLinearParams dialogRootParams() {
        return TemplateLinearParams.create(-1, -1).gravity(Gravity.CENTER);
    }

    public static TemplateLinearParams dialogTitleParams() {
        return TemplateLinearParams.create(-1, SIZE_DIALOG_TITLE).gravity(Gravity.CENTER_HORIZONTAL).leftMargin(SIZE_MARGIN_DEF).rightMargin(SIZE_MARGIN_DEF);
    }

    public static TemplateLinearParams editParams() {
        return TemplateLinearParams.create(SIZE_EDIT_WIDTH, SIZE_EDIT_HEIGHT).gravity(Gravity.CENTER_HORIZONTAL).topMargin(SIZE_MARGIN_DEF);
    }

    public static TemplateLinearParams buttonParams() {
        return TemplateLinearParams.create(0, -1).weight(1);
    }

    /**
     * 根据手机的分辨率从 DP 的单位 转成为 PX(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 PX(像素) 的单位 转成为 DP
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public interface OnClickListener {
        void onSuccess(TemplateUi view);

        void onFailure(TemplateUi view);

        void onCancel(TemplateUi view);
    }
}
