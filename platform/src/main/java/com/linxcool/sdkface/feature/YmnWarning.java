package com.linxcool.sdkface.feature;

import com.linxcool.sdkface.util.Logger;

/**
 * Created by huchanghai on 2017/9/7.
 */
public class YmnWarning {

    public interface WarningAttachment {
        void onBurst(String message);
    }

    private String message;
    private WarningAttachment runnable;

    public YmnWarning(String message) {
        this.message = message;
    }

    public YmnWarning setAttachment(WarningAttachment runnable) {
        this.runnable = runnable;
        return this;
    }

    public YmnWarning burst() {
        Logger.wRich(message);
        if (runnable != null) {
            runnable.onBurst(message);
        }
        return this;
    }

}
