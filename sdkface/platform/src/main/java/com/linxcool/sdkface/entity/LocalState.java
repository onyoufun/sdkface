package com.linxcool.sdkface.entity;

/**
 * Created by huchanghai on 2017/8/28.
 */
public abstract class LocalState extends Config{

    protected boolean interacted;

    public void setInteracted(boolean interacted) {
        this.interacted = interacted;
    }

    /**
     * 是否交互过
     */
    public boolean isInteracted() {
        return interacted;
    }

}
