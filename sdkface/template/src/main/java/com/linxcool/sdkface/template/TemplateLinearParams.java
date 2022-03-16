package com.linxcool.sdkface.template;

import android.widget.LinearLayout;

/**
 * Created by huchanghai on 2017/11/6.
 */

public class TemplateLinearParams {

    private LinearLayout.LayoutParams params;

    private TemplateLinearParams(LinearLayout.LayoutParams params) {
        this.params = params;
    }

    public static TemplateLinearParams create(int width, int height) {
        return new TemplateLinearParams(new LinearLayout.LayoutParams(width, height));
    }

    public static TemplateLinearParams create(LinearLayout.LayoutParams params) {
        return new TemplateLinearParams(params);
    }

    public LinearLayout.LayoutParams build() {
        return params;
    }

    public TemplateLinearParams width(int width) {
        params.width = width;
        return this;
    }

    public TemplateLinearParams height(int height) {
        params.height = height;
        return this;
    }

    public TemplateLinearParams gravity(int gravity) {
        params.gravity = gravity;
        return this;
    }

    public TemplateLinearParams weight(int weight) {
        params.weight = weight;
        return this;
    }

    public TemplateLinearParams leftMargin(int size) {
        params.leftMargin = size;
        return this;
    }

    public TemplateLinearParams topMargin(int size) {
        params.topMargin = size;
        return this;
    }

    public TemplateLinearParams rightMargin(int size) {
        params.rightMargin = size;
        return this;
    }

    public TemplateLinearParams bottomMargin(int size) {
        params.bottomMargin = size;
        return this;
    }

}
