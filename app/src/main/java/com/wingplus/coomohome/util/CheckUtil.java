package com.wingplus.coomohome.util;

import android.content.Context;
import android.content.pm.PackageInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leaffun.
 *         Create on 2017/9/27.
 */
public class CheckUtil {

    // 验证手机号是否为正确手机号
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^(0|86|17951)?(13[0-9]|15[0-9]|17[0-9]|18[0-9]|14[0-9])[0-9]{8}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    /**
     * 检测应用是否安装
     * @param context
     * @param packagename
     * @return
     */
    public static boolean checkMapAppsIsExist(Context context, String packagename){
        PackageInfo packageInfo;
        try{
            packageInfo = context.getPackageManager().getPackageInfo(packagename,0);
        }catch (Exception e){
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null){
            return false;
        }else{
            return true;
        }
    }
}
