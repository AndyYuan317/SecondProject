package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.ImageHelper;
import com.wingplus.coomohome.util.MapUtil;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.ToastUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.wingplus.coomohome.util.CheckUtil.checkMapAppsIsExist;

/**
 * 地图
 *
 * @author leaffun.
 *         Create on 2017/10/26.
 */
public class MapActivity extends BaseActivity implements View.OnClickListener {
    MapView mMapView = null;
    private BaiduMap map;
    private String name;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        init();
        if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, MapActivity.this)) {
            MapUtil.startLockMe();
            setMe();
        } else {
            PermissionUtil.applyFineLocation(MapActivity.this);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.nav:
                Intent intent = new Intent();

                //头部 添加相应地区
                String BAIDU_HEAD = "baidumap://map/direction?region=0";
                //起点的经纬度
                String BAIDU_ORIGIN = "&origin=";
                //终点的经纬度
                String BAIDU_DESTINATION = "&destination=";
                //路线规划方式 固定为transit、driving、navigation、walking，riding分别表示公交、驾车、导航、步行和骑行
                String BAIDU_MODE = "&mode=driving";
                //百度地图的包名
                String BAIDU_PKG = "com.baidu.BaiduMap";
                if (checkMapAppsIsExist(getApplicationContext(), BAIDU_PKG)) {

                    intent.setData(Uri.parse(BAIDU_HEAD
                            + BAIDU_ORIGIN
                            + RuntimeConfig.myLocation.latitude
                            + ","
                            + RuntimeConfig.myLocation.longitude
                            + BAIDU_DESTINATION
                            + RuntimeConfig.storeSelected.latitude
                            + ","
                            + RuntimeConfig.storeSelected.longitude
                            + BAIDU_MODE));
                    startActivity(intent);

                } else {

                    //头部 后面的sourceApplicaiton填自己APP的名字
                    String GAODE_HEAD = "androidamap://route?sourceApplication=BaiduNavi";
                    //起点经度
                    String GAODE_SLON = "&slon=";
                    //起点纬度
                    String GAODE_SLAT = "&slat=";
                    //起点名字
                    String GAODE_SNAME = "&sname=";
                    //终点经度
                    String GAODE_DLON = "&dlon=";
                    //终点纬度
                    String GAODE_DLAT = "&dlat=";
                    //终点名字
                    String GAODE_DNAME = "&dname=";
                    // dev 起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
                    // t = 1(公交) =2（驾车） =4(步行)
                    String GAODE_MODE = "&dev=0&t=2";
                    //高德地图包名
                    String GAODE_PKG = "com.autonavi.minimap";
                    if (checkMapAppsIsExist(getApplicationContext(), GAODE_PKG)) {
                        //Toast.makeText(MainActivity.this,"高德地图已经安装",Toast.LENGTH_SHORT).show();
                        LatLng mStart = new LatLng(RuntimeConfig.myLocation.latitude, RuntimeConfig.myLocation.longitude);
                        LatLng mEnd = new LatLng(RuntimeConfig.storeSelected.latitude, RuntimeConfig.storeSelected.longitude);
                        BD09ToGCJ02(mStart, mEnd);
                        intent.setAction("android.intent.action.VIEW");
                        intent.setPackage("com.autonavi.minimap");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse(GAODE_HEAD + GAODE_SLAT + mStart.latitude + GAODE_SLON + mStart.longitude +
                                GAODE_SNAME + "起点" + GAODE_DLAT + mEnd.latitude + GAODE_DLON + mEnd.longitude +
                                GAODE_DNAME + name + GAODE_MODE));
                        startActivity(intent);
                    } else {
                        ToastUtil.toast(getApplicationContext(), "请先安装百度或高德地图");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        map.clear();
        setMe();
        setStore();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


    private void init(){
        String location = getIntent().getStringExtra("location");
        String distance = getIntent().getStringExtra("distance");
        lat = getIntent().getDoubleExtra("lat", RuntimeConfig.myLocation.latitude);
        lng = getIntent().getDoubleExtra("lng", RuntimeConfig.myLocation.longitude);
        RuntimeConfig.setStoreSelected(new LatLng(lat, lng));
        name = getIntent().getStringExtra("name");
        TextView title = findViewById(R.id.title);
        TextView name_txt = findViewById(R.id.name_txt);
        TextView distance_txt = findViewById(R.id.distance_txt);
        title.setText("店铺");
        name_txt.setText(name);
        distance_txt.setText("地址: " + location);

        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        map = mMapView.getMap();
        map.setMyLocationEnabled(true);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);

        if (!MapUtil.isOPenGPS(MapActivity.this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
//            MapUtil.openGPS(MapActivity.this);
        } else {

            setMe();

            setStore();

//            drawLine();
        }
    }

    private void setMe() {
        if(RuntimeConfig.myLocation == null){
            ToastUtil.toast("定位失败");
            return;
        }

        LatLng point = new LatLng(RuntimeConfig.myLocation.latitude, RuntimeConfig.myLocation.longitude);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.address_me);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        map.addOverlay(option);


//        MyLocationData locData = new MyLocationData.Builder()
//                .direction(100)
//                .latitude(RuntimeConfig.myLocation.latitude)
//                .longitude(RuntimeConfig.myLocation.longitude)
//                .build();
//        map.setMyLocationData(locData);
//        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.address_map);
//        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, false, mCurrentMarker);
//        map.setMyLocationConfiguration(config);
    }

    private void setStore() {
        if(RuntimeConfig.storeSelected == null){
            ToastUtil.toast("未选择门店");
            return;
        }

        //定义Maker坐标点
        LatLng point = new LatLng(lat, lng);
//构建Marker图标

        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.store_address);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        map.addOverlay(option);


        MapStatus.Builder builder = new MapStatus.Builder();
        //设置缩放中心点；缩放比例；
        builder.target(point).zoom(14.0f);
        //给地图设置状态
        map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void drawLine() {
        List<LatLng> points = new ArrayList<>();
        points.add(RuntimeConfig.storeSelected);
        points.add(RuntimeConfig.myLocation);
        OverlayOptions ooPolyline = new PolylineOptions().width(3).color(0xAAFF0000).points(points);
        map.addOverlay(ooPolyline);
    }

    /**
     * 坐标转换
     *
     * @param mStart
     * @param mEnd
     */
    public void BD09ToGCJ02(LatLng mStart, LatLng mEnd) {
        LatLng newStart = convertBaiduToGPS(new LatLng(mStart.latitude, mStart.longitude));
        LatLng newEnd = convertBaiduToGPS(new LatLng(mEnd.latitude, mEnd.longitude));
        mStart = newStart;
        mEnd = newEnd;
    }

    /**
     * 将百度地图坐标转换成GPS坐标
     *
     * @param sourceLatLng
     * @return
     */
    public static LatLng convertBaiduToGPS(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
        double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
        BigDecimal bdLatitude = new BigDecimal(latitude);
        bdLatitude = bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal bdLongitude = new BigDecimal(longitude);
        bdLongitude = bdLongitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        return new LatLng(bdLatitude.doubleValue(), bdLongitude.doubleValue());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (MapUtil.isOPenGPS(MapActivity.this)) {
                setMe();

                setStore();

//                drawLine();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.FINE_LOCATION_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    //用户成功授权
                    if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, MapActivity.this)) {
                        MapUtil.startLockMe();
                        setMe();
                    } else {
                        ToastUtil.toast(this, "缺少位置权限,可能会影响定位");
                        PermissionUtil.goPermissionManager(MapActivity.this);
                    }
                } else {
                    //用户拒绝授权
                    ToastUtil.toast(this, "缺少位置权限,可能会影响定位");
                    PermissionUtil.goPermissionManager(MapActivity.this);
                }
                break;
            default:
                ToastUtil.toast(requestCode + "");
                break;
        }
    }
}
