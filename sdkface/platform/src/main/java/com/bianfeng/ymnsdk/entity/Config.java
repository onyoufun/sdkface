package com.bianfeng.ymnsdk.entity;

import com.google.gson.Gson;

/**
 * Created by huchanghai on 2017/8/29.
 */
public class Config {

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
