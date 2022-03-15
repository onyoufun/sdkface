package com.bianfeng.ymnsdk.feature;

import com.bianfeng.ymnsdk.YmnCallback;

/**
 * Created by huchanghai on 2017/8/30.
 */
public class YmnCallbackInterceptor implements YmnCallback {

    private YmnCallback callback;

    protected final void setNext(YmnCallback callback) {
        this.callback = callback;
    }

    /**
     * 重写改方法以作拦截用
     *
     * @param code 错误码
     * @param msg  错误信息
     * @return 返回true不继续向下传递，否者继续传递
     */
    public void onCallBack(int code, String msg) {
        dispatchNext(code, msg);
    }

    public final void dispatchNext(int code, String msg) {
        if (callback != null) {
            callback.onCallBack(code, msg);
        }
    }

}
