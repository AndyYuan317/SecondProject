package com.wingplus.coomohome.util;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

/**
 * @author leaffun.
 *         Create on 2017/12/26.
 */
public class BadgeUtil {
    /**
     * 华为手机添加角标
     */
    public static void setBadge(Context context, int number) {
        try {
            Bundle extra = new Bundle();
            extra.putString("package", "com.wingplus.coomohome");
            extra.putString("class", "com.wingplus.coomohome.activity.WelcomeActivity");
            extra.putInt("badgenumber", number);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
