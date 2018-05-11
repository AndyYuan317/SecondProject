package com.wingplus.coomohome.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author leaffun.
 *         Create on 2017/9/26.
 */
public class NetUtil {

    /**网络是否连接*/
    public static boolean IsActivityNetWork(Context context){
        ConnectivityManager connMgr =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
