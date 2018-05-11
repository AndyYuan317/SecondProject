package com.wingplus.coomohome.config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.wingplus.coomohome.application.MallApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局崩溃异常捕获
 * for: 处理异常，反馈信息
 * Created by wangyn on 16/9/8.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

//    private static final String PATH = ImageHelper.CACHE_PATH + "logs/";
    public static final String PATH = MallApplication.getContext().getExternalCacheDir() + "/logs/";
    public static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    private static CrashHandler mInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashHandler(){}

    public static CrashHandler getInstance(){
        return mInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        try {
//            dumpExceptionToSDCard(ex);
//            uploadExceptionToServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ex.printStackTrace();

        // 等待3秒,上传错误日志
//        try {
//            Thread.sleep(3 * 1000);
//        } catch (InterruptedException e){
//            e.printStackTrace();
//        }

        Process.killProcess(Process.myPid());
    }

    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            if (DEBUG) {
//                Log.w(TAG, "sdcard unmounted, skip dump exceptions");
//                return;
//            }
//        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

        try{
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            printWriter.println(time);
            dumpPhoneInfo(printWriter);
            printWriter.println();
            ex.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter printWriter) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        printWriter.print("App Version: ");
        printWriter.print(packageInfo.versionName);
        printWriter.print("_");
        printWriter.println(packageInfo.versionCode);

        printWriter.print("OS Version: ");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print("_");
        printWriter.println(Build.VERSION.SDK_INT);

        printWriter.print("Vendor: ");
        printWriter.println(Build.MANUFACTURER);

        printWriter.print("Model: ");
        printWriter.println(Build.MODEL);

        printWriter.print("CPU ABI: ");
        printWriter.println(Build.CPU_ABI);
    }

//    private void uploadExceptionToServer(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                File dir = new File(PATH);
//                if (dir.isDirectory()) {
//                    FileFilter filter = new FileFilter() {
//                        @Override
//                        public boolean accept(File file) {
//                            if (file.getName().endsWith(FILE_NAME_SUFFIX)) {
//                                return true;
//                            }
//                            return false;
//                        }
//                    };
//                    File[] files = dir.listFiles(filter);
//
//                    for(File file : files){
//                        try{
//                            HttpUtil.PostFile(WebAPI.getWebAPI(WebAPI.UPLOAD_LOG), file);
//                            file.delete();
//                        } catch (IOException e) {
//                            Log.e(TAG, "upload log to server failed");
//                        }
//                    }
//                }
//            }
//        }).start();
//    }
}
