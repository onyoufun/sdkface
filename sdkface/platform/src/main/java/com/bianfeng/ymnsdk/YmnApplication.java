package com.bianfeng.ymnsdk;

import android.app.Application;

import com.bianfeng.ymnsdk.util.SystemUtil;

/**
 * Created by huchanghai on 2017/8/25.
 */
public class YmnApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(SystemUtil.isMainProcess(this)) {
            YmnSdk.innerInit(this);
        }
    }

}
