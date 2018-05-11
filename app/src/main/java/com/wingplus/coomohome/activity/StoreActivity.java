package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.DisplayUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.MapUtil;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.Store;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.StoreResult;

import java.util.List;

/**
 * 店铺
 *
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class StoreActivity extends BaseActivity implements View.OnClickListener {


    private RecyclerView rv;
    private List<Store> ss;
    private double[] lat = null;
    private double[] lng = null;
    private StoreAdapter adapter;

    private GeoCoder mSearch = GeoCoder.newInstance();
    private OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                ToastUtil.toast(StoreActivity.this, result == null ? "地址编码失败" : result.getAddress());
            } else {
                //获取地理编码结果
                LogUtil.i(result.getAddress(), result.getLocation().toString());
                for (int i = 0; i < ss.size(); i++) {
                    Store store = ss.get(i);
                    if (store.getLocation().equals(result.getAddress())) {
                        LatLng latLng = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
                        store.setLatLng(latLng);
                        if(lat == null){
                            lat = new double[ss.size()];
                        }
                        lat[i] = latLng.latitude;
                        if(lng == null){
                            lng = new double[ss.size()];
                        }
                        lng[i] = latLng.longitude;
                        double distance = DistanceUtil.getDistance(RuntimeConfig.myLocation, store.getLatLng());
                        store.setDistance(PriceFixUtil.format(distance / 1000) + "km");
                        break;
                    }
                }
            }
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setData(ss);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            }
            //获取反向地理编码结果
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.toast("反向查询刷新");
                    adapter.setData(ss);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    private MapUtil.LockMeEnd lockMeEnd = new MapUtil.LockMeEnd() {
        @Override
        public void useMyLocation() {
            initData();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        initView();
        initData();
        MapUtil.registerMyLocation(lockMeEnd);
        if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, StoreActivity.this)) {
            MapUtil.startLockMe();
        } else {
            PermissionUtil.applyFineLocation(StoreActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapUtil.unRegisterMyLocation(lockMeEnd);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
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

    private void initView() {
        rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

        adapter = new StoreAdapter();
        rv.setAdapter(adapter);

        mSearch.setOnGetGeoCodeResultListener(listener);
    }

    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_STORE_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .getParams());

                final StoreResult storeResult = GsonUtil.fromJson(rs, StoreResult.class);

                if (storeResult != null && storeResult.getResult() == APIConfig.CODE_SUCCESS) {
                    ss = storeResult.getData();
//                    UIUtils.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.setData(ss);
//                            adapter.notifyDataSetChanged();
//
//                        }
//                    });
                    if (ss != null && ss.size() > 0) {
                        for (Store store : ss) {
                            mSearch.geocode(new GeoCodeOption().city(store.getLocation().substring(0, 1)).address(store.getLocation()));
                        }
                    }
                    mSearch.destroy();


                } else {
                    ToastUtil.toastByCode(storeResult);
                }
            }
        });
    }

    private class StoreAdapter extends RecyclerView.Adapter<StoreVH> {
        private List<Store> stores;

        private void setData(List<Store> stores){
            this.stores = stores;
        }

        @Override
        public StoreVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StoreVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_store, parent, false));
        }

        @Override
        public void onBindViewHolder(StoreVH holder, int position) {
            final int pos = position;
            holder.setData(stores.get(pos));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    Store store = stores.get(pos);
                    if(store.getLatLng() == null){
//                        ToastUtil.toast("地理位置是空");

                    }

                    intent.putExtra("location", store.getLocation());
                    intent.putExtra("distance", store.getDistance());
                    intent.putExtra("name", store.getName());
                    intent.putExtra("lat", lat[pos]);
                    intent.putExtra("lng", lng[pos]);
//                    if (store.getLatLng() != null || (RuntimeConfig.setStoreSelected(store.getLatLng()))) {
                        startActivity(intent);
                        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
//                    } else {
//                        ToastUtil.toast("选择门店" + (RuntimeConfig.storeSelected == null ? "失败" : "成功"));
//                    }

                }
            });

            if (position == 0) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.itemView.getLayoutParams());
                params.setMargins(DisplayUtil.dip2px(getApplicationContext(), 16)
                        , DisplayUtil.dip2px(getApplicationContext(), 14)
                        , DisplayUtil.dip2px(getApplicationContext(), 16)
                        , 0);
                holder.itemView.setLayoutParams(params);
            }
        }

        @Override
        public int getItemCount() {
            return stores == null ? 0 : ss.size();
        }
    }

    class StoreVH extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView name;
        private final TextView address;
        private final TextView distance;

        StoreVH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.store_name);
            address = itemView.findViewById(R.id.store_address);
            distance = itemView.findViewById(R.id.store_distance);
        }

        public void setData(Store s) {
            GlideUtil.GlideWithPlaceHolder(getApplicationContext(), s.getCover()).into(img);
            name.setText(s.getName());
            address.setText(s.getLocation());
            distance.setText(s.getDistance());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.FINE_LOCATION_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    //用户成功授权
                    if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, StoreActivity.this)) {
                        MapUtil.startLockMe();

                    } else {
                        ToastUtil.toast(this, "缺少位置权限,可能会影响定位");
                        PermissionUtil.goPermissionManager(StoreActivity.this);
                    }
                } else {
                    //用户拒绝授权
                    ToastUtil.toast(this, "缺少位置权限,可能会影响定位");
                    PermissionUtil.goPermissionManager(StoreActivity.this);
                }
                break;
            default:
                ToastUtil.toast(requestCode + "");
                break;
        }
    }
}
