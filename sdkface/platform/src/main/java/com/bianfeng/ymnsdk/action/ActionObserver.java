package com.bianfeng.ymnsdk.action;

import java.util.Observable;
import java.util.Observer;

/**
 * 网络动作观察者
 *
 * @author 胡昌海(linxcool.hu)
 */
public abstract class ActionObserver implements Observer {

    @Override
    public void update(Observable observable, Object data) {
        onActionResult((ActionSupport.ResponseResult) data);
    }

    /**
     * 基于ActionSupport的观察者，如果传入的是Activity那么能够保证以下代码在主线程中执行
     * @param result
     */
    public abstract void onActionResult(ActionSupport.ResponseResult result);

}
