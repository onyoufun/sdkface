package com.bianfeng.ymnsdk.action;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * HTTP工具类
 *
 * @author: linxcool.hu
 */
public class HttpHelper {

    public static final String VERSION_SERVER = "v1";

    /**
     * HTTP常量-GET请求
     */
    public static final int HTTP_METHOD_GET = 1;
    /**
     * HTTP常量-POST请求
     */
    public static final int HTTP_METHOD_POST = 2;
    /**
     * HTTP常量-请求限制时间
     */
    public static final int HTTP_REQ_LIMIT_TIME = 15 * 1000;
    /**
     * HTTP常量-响应限制时间
     */
    public static final int HTTP_RES_LIMIT_TIME = 25 * 1000;

    // 返回码
    public static final int CODE_RES_SUCCESS = 0;
    public static final int CODE_REQ_TIME_OUT = 600;
    public static final int CODE_UNKNOW_ERROR = 601;
    public static final int CODE_SERVER_ERROR = 602;
    public static final int CODE_UNSET = 999;

    private Context context;
    private int errorCode;
    private String errorMsg;

    private String method;

    public HttpHelper(Context context) {
        this.context = context;
    }

    public void setMethod(int method) {
        this.method = method == 1 ? "GET" : "POST";
    }

    public boolean isGetMethod() {
        return method.equals("GET");
    }

    public void createHttpRequest(final String urlString, final String params, final HttpListener listerner) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //进行访问网络操作
                URL url = null;

                try {
                    url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if (getProxy(context,url) != null) {
                        connection = getProxy(context,url);
                    }
                    connection.setReadTimeout(HTTP_RES_LIMIT_TIME);
                    connection.setConnectTimeout(HTTP_REQ_LIMIT_TIME);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/json,charset=UTF-8");//x-www-form-urlencoded
                    if (method.equals("POST")) {
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                    } else {
                    }
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(String.valueOf(params));
                    out.flush();
                    out.close();
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        int len = 0;
                        byte[] buf = new byte[1024 * 1024];
                        StringBuilder jsonBuilder = new StringBuilder();
                        while ((len = is.read(buf)) != -1) {
                            jsonBuilder.append(new String(buf, 0, len));
                        }
                        is.close();
                        String infoStr = jsonBuilder.toString();
                        listerner.onComplete(infoStr);

                    } else {
                        listerner.onError(connection.getResponseCode(), connection.getResponseMessage());

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 获取网络代理信息
     *
     * @param context
     * @return
     */
    public static HttpURLConnection getProxy(Context context,URL url) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isAvailable() || ni.getType() != ConnectivityManager.TYPE_MOBILE){
            return null;
        }

        String proxyHost = android.net.Proxy.getDefaultHost();
        int port = android.net.Proxy.getDefaultPort();
        if (proxyHost != null && port != -1) {
            try {
                SocketAddress sa = new InetSocketAddress(proxyHost, port);
                Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, sa);
                return (HttpURLConnection)url.openConnection(proxy);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
