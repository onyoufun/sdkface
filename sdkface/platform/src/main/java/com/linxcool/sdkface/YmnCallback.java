package com.linxcool.sdkface;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huchanghai on 2017/8/29.
 */
public interface YmnCallback {

    void onCallBack(int code, String msg);

    public class RichCallbackMessage {
        final static String TAG_START = "_YMN_RCM_ST_";
        final static String TAG_SEPARATOR = "_YMN_RCM_SE_";
        final static String TAG_NULL = "_YMN_RCM_NU_";
        final static String TAG_EMPTY = "_YMN_RCM_EM_";

        private Object data;
        private Object ext;

        public Object getData() {
            return data;
        }

        public Object getExt() {
            return ext;
        }

        public RichCallbackMessage(Object data, Object ext) {
            this.data = data;
            this.ext = ext;
        }

        private RichCallbackMessage() {
        }

        public static RichCallbackMessage instance(String text) {
            text = text.substring(TAG_START.length());
            String[] arrays = text.split(TAG_SEPARATOR);

            RichCallbackMessage message = new RichCallbackMessage();
            message.data = stringAsObject(arrays[0]);
            message.ext = stringAsObject(arrays[1]);

            return message;
        }

        public static boolean isRich(String text) {
            return !TextUtils.isEmpty(text) && text.startsWith(TAG_START);
        }

        private static String objectAsString(Object object) {
            if (object == null) return TAG_NULL;
            if (TextUtils.isEmpty(object.toString())) return TAG_EMPTY;
            return object.toString();
        }

        private static Object stringAsObject(String string) {
            try {
                if (TAG_NULL.equals(string)) {
                    return null;
                }
                if (TAG_EMPTY.equals(string)) {
                    return "";
                }
                return new JSONObject(string);
            } catch (JSONException e) {
                return string;
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(TAG_START);
            builder.append(objectAsString(data));
            builder.append(TAG_SEPARATOR);
            builder.append(objectAsString(ext));
            return builder.toString();
        }
    }

}
