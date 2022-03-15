package com.bianfeng.ymnsdk.util;

import android.util.Log;

import java.util.LinkedList;

/**
 * API for sending log output.
 * <p>
 * <p>
 * Generally, use the Log.v() Log.d() Log.i() Log.w() and Log.e() methods.
 * <p>
 * <p>
 * The order in terms of verbosity, from least to most is ERROR, WARN, INFO,
 * DEBUG, VERBOSE. Verbose should never be compiled into an application except
 * during development. Debug logs are compiled in but stripped at runtime.
 * Error, warning and info logs are always kept.
 * <p>
 * <p>
 * <b>Tip:</b> A good convention is to declare a <code>TAG</code> constant in
 * your class:
 * <p>
 * <pre>
 * private static final String TAG = &quot;MyActivity&quot;;
 * </pre>
 * <p>
 * and use that in subsequent calls to the log methods.
 * </p>
 * <p>
 * <p>
 * <b>Tip:</b> Don't forget that when you make a call like
 * <p>
 * <pre>
 * Log.v(TAG, &quot;index=&quot; + i);
 * </pre>
 * <p>
 * that when you're building the string to pass into Log.d, the compiler uses a
 * StringBuilder and at least three allocations occur: the StringBuilder itself,
 * the buffer, and the String object. Realistically, there is also another
 * buffer allocation and copy, and even more pressure on the gc. That means that
 * if your log message is filtered out, you might be doing significant work and
 * incurring significant overhead.
 */
public class Logger {

    public static final String TAG = "YmnSdk";

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    private static String[] COLORS = {"#66007F", "#66007F", "#66007F", "#66007F", "#3A7F00", "#FF7F00", "#ff0000"};

    private static LinkedList<String> logs;

    private static boolean showDebugLog;

    private Logger() {
    }

    public static void showDebugLog(boolean showDebugLog) {
        Logger.showDebugLog = showDebugLog;
    }

    public static void updateState() {
        if (ResourceUtil.isSdcardReady()) {
            if (ResourceUtil.isSdcardFileExist("bianfeng/sdk/debug")) {
                showDebugLog = true;
            }
        }
        System.out.print("state of showDebugLog is " + showDebugLog);
    }

    public static void setLogToCache(boolean b) {
        if (b) {
            logs = new LinkedList<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                public void addLast(String object) {
                    if (size() > 5000) {
                        removeFirst();
                    }
                    super.addLast(object);
                }
            };
        } else {
            logs = null;
        }
    }

    public static StringBuilder getCacheLog() {
        StringBuilder builder = new StringBuilder();
        for (String log : logs) {
            builder.append(log);
        }
        return builder;
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int v(String tag, String msg) {
        return printlog(VERBOSE, tag, msg);
    }

    public static int v(String msg) {
        return v(TAG, msg);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int d(String tag, String msg) {
        int number = -1;
        if (showDebugLog) printlog(DEBUG, tag, msg);
        return number;
    }

    public static int d(String msg) {
        return d(TAG, msg);
    }

    public static int dRich(String msg) {
        return d(TAG, rich(msg));
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int i(String tag, String msg) {
        int number = -1;
        if (showDebugLog) number = printlog(INFO, tag, msg);
        return number;
    }

    public static int i(String msg) {
        return i(TAG, msg);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int w(String tag, String msg) {
        return printlog(WARN, tag, msg);
    }

    public static int w(String msg) {
        return w(TAG, msg);
    }

    public static int wRich(String msg) {
        return w(TAG, rich(msg));
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int e(String tag, String msg) {
        return printlog(ERROR, tag, msg);
    }

    public static int e(String msg) {
        return e(TAG, msg);
    }

    public static int eRich(String msg) {
        return e(TAG, rich(msg));
    }

    public static int printlog(int priority, String tag, String msg) {
        if (logs != null) {
            String htmlMsg = msg.replace("\n", "<br/>");
            logs.addLast(String.format("<font color='%s'>【%s】<br/>%s</font><br/><br/>", COLORS[priority], tag, htmlMsg));
        }
        return Log.println(priority, tag, msg);
    }

    public static String rich(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append("---------------------------------------------->>");
        if (!msg.startsWith("\n")) builder.append("\n");
        builder.append(msg);
        if (!msg.endsWith("\n")) builder.append("\n");
        builder.append("<<----------------------------------------------");
        return builder.toString();
    }
}
