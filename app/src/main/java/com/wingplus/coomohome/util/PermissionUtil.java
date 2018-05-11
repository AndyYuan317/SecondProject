package com.wingplus.coomohome.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.wingplus.coomohome.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author leaffun.
 *         Create on 2017/9/26.
 */
public class PermissionUtil {
    public static final int CAMERA_REQUEST_CODE = 200;
    public static final int FINE_LOCATION_REQUEST_CODE = 201;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 202;

    public static String CAMERA_STRING = android.Manifest.permission.CAMERA;
    public static String FINE_LOCATION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;
    public static String READ_EXTERNAL_STORAGE_STR = Manifest.permission.READ_EXTERNAL_STORAGE;


    @TargetApi(Build.VERSION_CODES.M)
    public static void applyCamera(Activity activity) {
        if (activity.shouldShowRequestPermissionRationale(CAMERA_STRING)) {
            //用户勾选询问
            //小米4 6.0没有询问机制，拒绝一次就无法开启权限，帮助打开权限设置界面
        }
        //主要功能需要权限,再次引导用户打
        String[] perms = {CAMERA_STRING};
        activity.requestPermissions(perms, CAMERA_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void applyFineLocation(Activity activity) {
        if (activity.shouldShowRequestPermissionRationale(FINE_LOCATION_STRING)) {
            //用户勾选询问
        }
        //主要功能需要权限,再次引导用户打
        String[] perms = {FINE_LOCATION_STRING};
        activity.requestPermissions(perms, FINE_LOCATION_REQUEST_CODE);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean applyReadExternalStorage(Activity activity) {
        boolean neverToast;
        if (activity.shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE_STR)) {
            //用户勾选询问
            neverToast = false;
            String[] perms = {READ_EXTERNAL_STORAGE_STR};
            activity.requestPermissions(perms, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            //用户禁止，辅助功能不再打扰用户
            neverToast = true;
        }
        return neverToast;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermission(String permission, Context context) {
        if (needCheck()) {
            return (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    private static boolean needCheck() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    /**
     * 打开权限设置
     *
     * @param activity
     */
    public static void goPermissionManager(Activity activity) {
        gotoMiuiPermission(activity);
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private static void gotoMiuiPermission(Activity activity) {
        try {
            if (isMIUI()) {
                try {
                    // MIUI 8
                    Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                    localIntent.putExtra("extra_pkgname", activity.getPackageName());
                    activity.startActivity(localIntent);
                } catch (Exception e) {
                    try {
                        // MIUI 5/6/7
                        Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                        localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                        localIntent.putExtra("extra_pkgname", activity.getPackageName());
                        activity.startActivity(localIntent);
                    } catch (Exception e1) {
                        // 否则跳转到应用详情
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivity(intent);
                    }
                }
            } else {
                gotoMeizuPermission(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            gotoMeizuPermission(activity);
        }
    }

    /**
     * 判断是否是MIUI
     */
    private static boolean isMIUI() {
        String device = Build.MANUFACTURER;
        if (device.equals("Xiaomi")) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                return prop.getProperty("ro.miui.ui.version.code", null) != null
                        || prop.getProperty("ro.miui.ui.version.name", null) != null
                        || prop.getProperty("ro.miui.internal.storage", null) != null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private static void gotoMeizuPermission(Activity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoHuaweiPermission(activity);
        }
    }

    /**
     * 华为的权限管理页面
     */
    private static void gotoHuaweiPermission(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            activity.startActivity(getAppDetailSettingIntent(activity));
        }
    }

    /**
     * 打开应用详情页面intent
     */
    private static Intent getAppDetailSettingIntent(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        return localIntent;
    }
}
