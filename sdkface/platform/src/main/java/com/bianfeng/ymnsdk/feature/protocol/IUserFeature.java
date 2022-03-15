package com.bianfeng.ymnsdk.feature.protocol;

import android.text.TextUtils;

import java.util.LinkedHashMap;

/**
 * 用户相关
 *
 * @author huchanghai
 */
public interface IUserFeature {

    String FUNCTION_IS_LOGINED = "isLogined";
    String FUNCTION_LOGOUT = "logout";
    String FUNCTION_SHOW_TOOLBAR = "showToolBar";
    String FUNCTION_HIDE_TOOLBAR = "hideToolBar";
    String FUNCTION_ACCOUNT_SWITCH = "accountSwitch";
    String FUNCTION_EXIT = "exit";
    String FUNCTION_SUBMIT_USERINFO = "submitUserInfo";
    String FUNCTION_GET_USER_INFO = "getUserInfo";
    String FUNCTION_ENTER_PLATFORM = "enterPlatform";

    /**
     * SDK内部通信时发送给服务端的是uname，但响应回的是username
     */
    String LOGIN_SUC_RS_UID = "uid";
    String LOGIN_SUC_RS_UNAME = "uname";
    String LOGIN_SUC_RS_NICKNAME = "nickName";
    String LOGIN_SUC_RS_SESSION = "session";
    String LOGIN_SUC_RS_EXT = "ext";

    /**
     * 纯三方登录
     */
    void login();

    boolean isLogined();

    void logout();

    void showToolBar();

    void hideToolBar();

    void switchAccount();

    void exit();

    void submitUserInfo(LinkedHashMap<String, String> data);

    /**
     * 返回用户信息
     *
     * @return
     */
    UserInfo getUserInfo();

    void enterPlatform();

    public class UserInfo {

        private String ymnUserId;
        private String ymnUserIdInt;
        private String ymnSession;
        private String ymnUserName;
        private boolean ymnLogined;

        public String platformUserId;
        public String platformSession;
        public String platformUserName;
        public boolean platformLogined;

        public Object resExt;

        public String getUserName() {
            if (TextUtils.isEmpty(platformUserName)) return ymnUserName;
            return platformUserName;
        }

        public void setYmnUserId(String ymnUserId) {
            this.ymnUserId = ymnUserId;
        }

        public String getYmnUserIdInt() {
            return ymnUserIdInt;
        }

        public void setYmnUserIdInt(String ymnUserIdInt) {
            this.ymnUserIdInt = ymnUserIdInt;
        }

        public void setYmnSession(String ymnSession) {
            this.ymnSession = ymnSession;
        }

        public String getYmnSession() {
            return ymnSession;
        }

        public void setYmnUserName(String ymnUserName) {
            this.ymnUserName = ymnUserName;
        }

        public boolean isYmnLogined() {
            return ymnLogined;
        }

        public void setYmnLogined(boolean ymnLogined) {
            this.ymnLogined = ymnLogined;
        }

        public String getPlatformUserId() {
            return platformUserId;
        }

        public void setPlatformUserId(String platformUserId) {
            this.platformUserId = platformUserId;
        }

        public void setPlatformSession(String platformSession) {
            this.platformSession = platformSession;
        }

        public void setPlatformUserName(String platformUserName) {
            this.platformUserName = platformUserName;
        }

        public void setPlatformLogined(boolean platformLogined) {
            this.platformLogined = platformLogined;
        }

        public void setResponseExt(Object resExt) {
            this.resExt = resExt;
        }

        public <T> T getResonseExt() {
            return (T) resExt;
        }
    }
}
