package com.wingplus.coomohome.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.wingplus.coomohome.application.MallApplication;


public class UIUtils {
    /**
     * 重写getResource()
     */
    public static Resources getResource() {
        return MallApplication.getApplication().getResources();
    }

    /**
     * 获取上下文
     */
    public static Context getContext() {
        return MallApplication.getContext();
    }

    /**
     * 获取消息处理器
     */
    public static Handler getHandler() {
        return MallApplication.getHandler();
    }

    /**
     * 获取主线程
     */
    public static int getMainThread() {
        return MallApplication.getMainThreadId();
    }

    ///////////////////// 加载资源文件 ///////////////////////////////////////


    /**
     * 获取到字符串
     *
     * @param id 字符串的id
     */
    public static String getString(int id) {
        return getResource().getString(id);
    }

    /**
     * 获取到字符数组
     *
     * @param id 字符数组的id
     */
    public static String[] getStringArray(int id) {
        return getResource().getStringArray(id);
    }

    /**
     * 获取颜色
     */
    public static int getColor(int id) {
        return getResource().getColor(id);
    }

    /**
     * 获取图片
     */
    public static Drawable getDrawable(int id) {
        return getResource().getDrawable(id);
    }

    /**
     * 获取尺寸(像素)
     */
    public static int getDimen(int id) {
        return getResource().getDimensionPixelSize(id);// 具体像素值px
    }

    /**
     * 根据id获取颜色的状态选择器
     */
    public static ColorStateList getColorStateList(int id) {
        return getResource().getColorStateList(id);
    }

    ///////////////////// dip和px转换  ///////////////////////////////////////

    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        final float scale = getResource().getDisplayMetrics().density; // 像素密度
        return (int) (dip * scale + 0.5f); // 四舍五入
    }

    /**
     * px转换dip
     */
    public static int px2dip(int px) {
        final float scale = getResource().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    //////////////////////// 加载布局文件 ////////////////////////////////////////////

    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    ////////////////////////// 判断是否运行在主线程 /////////////////////////////////////////////
    public static boolean isRunOnUiThread() {
        int myTid = android.os.Process.myTid();
        if (myTid == getMainThread()) return true;
        return false;
    }

    public static void runOnUIThread(Runnable r) {
        if (isRunOnUiThread()) {
            r.run(); // 已经是主线程，直接运行
        } else {
            getHandler().post(r); // 如果是子线程，借助handler让其运行在主线程
        }
    }

    public static void runOnUIThread(Runnable r, long s) {

            getHandler().postDelayed(r, s); // 如果是子线程，借助handler让其运行在主线程

    }

    public static String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) MallApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

}
