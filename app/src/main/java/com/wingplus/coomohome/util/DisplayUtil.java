package com.wingplus.coomohome.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 显示工具类
 */
public class DisplayUtil {
    /**dp转px*/
	public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;                   
        return (int)(dipValue * scale + 0.5f);           
    }

    /**px转dp*/
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;                   
        return (int)(pxValue / scale + 0.5f);           
    }

    /**屏幕显示信息*/
    public static DisplayMetrics getScreenDm(Activity activity){
    	DisplayMetrics dm = new DisplayMetrics();
    	activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    	
    	return dm;
    }

    /**屏幕宽度px*/
    public static int getScreenWidth(Activity activity){
    	return getScreenDm(activity).widthPixels;
    }

    /**屏幕高度px*/
    public static int getScreenHeight(Activity activity){
    	return getScreenDm(activity).heightPixels;
    }

    /**通过屏幕宽度，获取适配的比例*/
    public static float getAutoFitScale(Activity activity, int width){
    	int screenWidth = getScreenWidth(activity);
    	return (float)screenWidth / width;
    }

    /**控制字符串不超过10个字符*/
    public static String handleWithTitle(String originalTitle){
    	if (originalTitle == null){
    		return "";
    	}else if (originalTitle.length() > 10){
    		return originalTitle.substring(0, 10) + "...";
    	}else{
    		return originalTitle;
    	}
    }
}
