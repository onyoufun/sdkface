package com.bianfeng.ymnsdk.action;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.bianfeng.ymnsdk.YmnSdk;
import com.bianfeng.ymnsdk.util.SystemUtil;

/**
 * Created by huchanghai on 2017/9/6.
 */

public interface ActionAttachment {

    void onStart(Context context);

    void onEnd(Context context);

    public class ProgressAttachment implements ActionAttachment {

        ProgressDialog progressDialog;

        protected void dismissProgressDialog() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }

        protected void showProgressDialog(Context context) {
            dismissProgressDialog();
            progressDialog = ProgressDialog.show(context, null, "  加载中···  ");
        }

        @Override
        public void onStart(final Context context) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(context);
                    }
                });
            } else {
                showProgressDialog(context);
            }
        }

        @Override
        public void onEnd(Context context) {
            dismissProgressDialog();
            if (context instanceof Activity) {
                SystemUtil.hideVirtualKey((Activity) context);
            }
        }
    }
}
