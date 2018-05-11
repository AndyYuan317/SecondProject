package com.wingplus.coomohome.config;

import android.util.Log;

import com.wingplus.coomohome.BuildConfig;

/**
 * 日志打印等级控制类，发布前设置 DEBUG = false
 * for: 防止未清除的调试日志
 * @author leaffun.
 *         Create on 2017/8/29.
 */
public class LogUtil {

    private static final boolean DEBUG = false;
    private static final int LEVEL_E = 5;
    private static final int LEVEL_W = 4;
    private static final int LEVEL_I = 3;
    private static final int LEVEL_D = 2;
    private static final int LEVEL_V = 1;
    private static int level = 5;

    public static void setLevel(int nowLevel) {
        level = nowLevel;
    }

    public static void e(String tag, String error) {
        if (DEBUG && level >= LEVEL_E) {
            Log.e(tag, error);
        }
    }

    public static void w(String tag, String warn) {
        if (DEBUG && level >= LEVEL_W) {
            Log.e(tag, warn);
        }
    }

    public static void i(String tag, String info) {
        if (DEBUG && level >= LEVEL_I) {
            Log.i(tag, info);
        }
    }

    public static void d(String tag, String debug) {
        if (DEBUG && level >= LEVEL_D) {
            Log.d(tag, debug);
        }
    }

    public static void v(String tag, String verbose) {
        if (DEBUG && level >= LEVEL_V) {
            Log.v(tag, verbose);
        }
    }
}
