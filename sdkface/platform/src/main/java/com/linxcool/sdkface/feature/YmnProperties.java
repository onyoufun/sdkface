package com.linxcool.sdkface.feature;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.linxcool.sdkface.feature.protocol.IPlugin;
import com.linxcool.sdkface.util.DecodeUtil;
import com.linxcool.sdkface.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class YmnProperties extends Properties {

    private static final long serialVersionUID = 1L;

    private static final String[][] NAMES = {
            {"appid", "appId", "appID", "AppId", "Appid", "app_id",},
            {"appkey", "appKey", "AppKey", "Appkey", "app_key",},
            {"secretkey", "secretKey", "Secretkey", "SecretKey", "secret_key", "appsecret", "appSecret", "Appsecret", "AppSecret", "app_secret",},
            {"platPublicKey", "publicRsaKey", "publickey", "publicKey", "public_key",},
            {"privateKey", "privatekey", "appPrivateKey", "private_key",},
            {"payid", "payId", "pay_id",},
            {"cpid", "cpId", "cp_id",},
            {"gameid", "gameId", "game_id",},
    };

    private static YmnProperties properties;
    private static JSONObject jsoncfgs;

    public static void init(Context context) {
        try {
            byte[] buf = null;
            AssetManager am = context.getAssets();
            if (ResourceUtil.assetFileExist(context, "sdkface.cfg")) {
                buf = ResourceUtil.InputStreamToByte(am.open("sdkface.cfg"));
                buf = DecodeUtil.decode(buf);
            }
            if (ResourceUtil.assetFileExist(context, "sdkface-debug.cfg")) {
                buf = ResourceUtil.InputStreamToByte(am.open("sdkface-debug.cfg"));
            }
            boolean tryNext = buf != null;
            if (tryNext) {
                tryNext = !readAsJson(new String(buf));
            }
            if (tryNext) {
                readAsProperties(new String(buf));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean readAsJson(String text) {
        try {
            jsoncfgs = new JSONObject(text);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private static boolean readAsProperties(String text) {
        try {
            properties = new YmnProperties();
            properties.load(new StringReader(text));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getPluginValue(IPlugin plugin, String name) {
        if (jsoncfgs != null) {
            try {
                JSONObject json = jsoncfgs.optJSONObject(plugin.getPluginName());
                if (json != null) return json.optString(name);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        if (properties != null) {
            return properties.getProperty(name);
        }
        return null;
    }

    public static String getValue(String name) {
        if (jsoncfgs != null) {
            return jsoncfgs.optString(name);
        }
        if (properties != null) {
            return properties.getProperty(name);
        }
        return null;
    }

    private final List<?>[] namesArray;

    private YmnProperties() {
        namesArray = new List[NAMES.length];
        for (int i = 0; i < NAMES.length; i++) {
            namesArray[i] = Arrays.asList(NAMES[i]);
        }
    }

    @Override
    public String getProperty(String name) {
        String value = super.getProperty(name);
        if (!TextUtils.isEmpty(value)) {
            return value.trim();
        }

        for (int i = 0; i < NAMES.length; i++) {
            if (namesArray[i].contains(name)) return getValue(namesArray[i]);
        }

        return value;
    }

    private String getValue(List<?> keys) {
        for (Object key : keys) {
            String value = super.getProperty(String.valueOf(key));
            if (!TextUtils.isEmpty(value)) return value;
        }
        return null;
    }

}
