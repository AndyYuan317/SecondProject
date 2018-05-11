package com.wingplus.coomohome.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.RuntimeConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leaffun.
 *         Create on 2017/10/27.
 */
public class MapUtil {

    public static List<LockMeEnd> registerList = new ArrayList<>();

    public static LocationClient mLocationClient = null;

    public static BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            RuntimeConfig.myLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            mLocationClient.stop();
            if (registerList != null && registerList.size() > 0){
                for(LockMeEnd lockMeEnd : registerList){
                    lockMeEnd.useMyLocation();
                }
            }
        }
    };

    /**
     * 初始化定位配置
     */
    public static synchronized void setLocation() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(MallApplication.getContext());

            mLocationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();

            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

            option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认gcj02
//gcj02：国测局坐标；
//bd09ll：百度经纬度坐标；
//bd09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

            option.setScanSpan(1000);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

            option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

            option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

            option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)


            option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

            mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        }
    }

    /**
     * 定位自己，成功后将定位信息保存至RuntimeConfig
     */
    public static void startLockMe() {
        setLocation();
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                mLocationClient.start();
                mLocationClient.requestLocation();
            }
        });
    }

    public interface LockMeEnd {
        void useMyLocation();
    }

    /**
     * 注册自我定位监听
     * @param lockMeEnd
     */
    public static void registerMyLocation(LockMeEnd lockMeEnd) {
        if (registerList != null) {
            registerList.add(lockMeEnd);
        }
    }

    /**
     * 注销自我定位监听
     * @param lockMeEnd
     */
    public static void unRegisterMyLocation(LockMeEnd lockMeEnd) {
        if (registerList != null && registerList.contains(lockMeEnd)) {
            registerList.remove(lockMeEnd);
        }
    }

    /**
     * 强制帮用户打开GPS，非引导
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断GPS是否开启
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPenGPS(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }

        return false;
    }
}
