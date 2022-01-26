package com.junwu.demo.Utils;

import android.util.Log;

import com.junwu.demo.BaseApplication;

/**
 * 创建日期：2022/1/26
 *
 * @author jun.wu
 * 文件名称：PaiAppLog.java
 * 类说明：Log工具类
 */
public class PaiAppLog {
    public static final String GLOBAL_TAG = "PaiMusic."
            + Utils.getVersion(BaseApplication.sApplicationContext);
    private static final int STACK_TRACE = 4;

    /**
     * {@link #DEBUG} is true if running in 'userdebug' variant.
     * The following log is disabled in 'user' variant.
     */
    public static final boolean DEBUG;

    /**
     * logging flag for performance measurement.
     */
    public static final boolean IS_TIME_DEBUG = false;

    /**
     * {@link #VERBOSE} is true if running in 'userdebug' variant and
     * log level is higher than VERBOSE.
     */
    public static final boolean VERBOSE;

    static {
        DEBUG = true;
        // Build.TYPE.equals("userdebug");
        VERBOSE = DEBUG && Log.isLoggable(GLOBAL_TAG, Log.VERBOSE);
    }
    // CHECKSTYLE:OFF

    /**
     * Enabled if running in 'userdebug' variant and Log level is higher than VERBOSE.
     */
    public static void v(String... message) {
        if (VERBOSE) {
            Log.v(GLOBAL_TAG, makeLogStringWithLongInfo(message));
        }
    }

    /**
     * Enabled if running in 'userdebug' variant and Log level is higher than VERBOSE.
     */
    public static void v(String message, Throwable throwable) {
        if (VERBOSE) {
            Log.v(GLOBAL_TAG, makeLogStringWithLongInfo(message), throwable);
        }
    }

    /**
     * Enabled if running in 'userdebug' variant or Log level is higher than DEBUG.
     */
    public static void d(String... message) {
        if (DEBUG || Log.isLoggable(GLOBAL_TAG, Log.DEBUG)) {
            Log.d(GLOBAL_TAG, makeLogStringWithLongInfo(message));
        }
    }

    /**
     * Enabled if running in 'userdebug' variant or Log level is higher than DEBUG.
     */
    public static void d(String message, Throwable throwable) {
        if (DEBUG || Log.isLoggable(GLOBAL_TAG, Log.DEBUG)) {
            Log.d(GLOBAL_TAG, makeLogStringWithLongInfo(message), throwable);
        }
    }

    /**
     * Always enabled.
     */
    public static void i(String... message) {
        Log.i(GLOBAL_TAG, makeLogStringWithShortInfo(message));
    }

    /**
     * Always enabled.
     */
    public static void i(String message, Throwable throwable) {
        Log.i(GLOBAL_TAG, makeLogStringWithShortInfo(message), throwable);
    }

    /**
     * Always enabled.
     */
    public static void w(String... message) {
        Log.w(GLOBAL_TAG, makeLogStringWithShortInfo(message));
    }

    /**
     * Always enabled.
     */
    public static void w(String message, Throwable throwable) {
        Log.w(GLOBAL_TAG, makeLogStringWithShortInfo(message), throwable);
    }

    /**
     * Always enabled.
     */
    public static void e(String... message) {
        Log.e(GLOBAL_TAG, makeLogStringWithShortInfo(message));
    }

    /**
     * Always enabled.
     */
    public static void e(String message, Throwable throwable) {
        Log.e(GLOBAL_TAG, makeLogStringWithShortInfo(message), throwable);
    }
    // CHECKSTYLE:ON

    private static String makeLogStringWithLongInfo(String... message) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_TRACE];
        StringBuilder builder = new StringBuilder();
        appendTag(builder, stackTrace);
        appendTraceInfo(builder, stackTrace);
        for (String i : message) {
            builder.append(" ");
            builder.append(i);
        }
        return builder.toString();
    }

    private static String makeLogStringWithShortInfo(String... message) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[STACK_TRACE];
        StringBuilder builder = new StringBuilder();
        appendTag(builder, stackTrace);
        for (String i : message) {
            builder.append(" ");

            builder.append(i);
        }
        return builder.toString();
    }

    private static void appendTag(StringBuilder builder, StackTraceElement stackTrace) {
        builder.append("[");
        builder.append(suppressFileExtension(stackTrace.getFileName()));
        builder.append("]" + " ");
    }

    private static void appendTraceInfo(StringBuilder builder, StackTraceElement stackTrace) {
        builder.append(stackTrace.getMethodName());
        builder.append(":");
        builder.append(stackTrace.getLineNumber());
        builder.append(" ");
    }

    private static String suppressFileExtension(String filename) {
        int extensionPosition = filename.lastIndexOf(".");
        if (extensionPosition > 0 && extensionPosition < filename.length()) {
            return filename.substring(0, extensionPosition);
        } else {
            return filename;
        }
    }
}