package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.MapUtil;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.OrderAddressResult;

import java.util.List;

/**
 * 定位页
 *
 * @author leaffun.
 *         Create on 2017/9/6.
 */
public class LocationActivity extends BaseActivity implements View.OnClickListener {

    private LatLng choseLocation = null;

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) {
            if (result != null) {
                poiList = result.getAllPoi();
                adapter.notifyDataSetChanged();
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        }
    };

    private OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
            } else {
                //获取地理编码结果
            }
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            } else {
                //获取反向地理编码结果，根据反地编获取附近地址，刷新地址列表，重新定位->定位自己
                RuntimeConfig.USER_ADDRESS = result.getAddress();
                poiList = result.getPoiList();
                adapter.notifyDataSetChanged();
            }
        }
    };

    MapUtil.LockMeEnd lockMeEnd = new MapUtil.LockMeEnd() {
        @Override
        public void useMyLocation() {
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(RuntimeConfig.myLocation));

        }
    };

    private PoiSearch mPoiSearch = PoiSearch.newInstance();
    private GeoCoder mSearch = GeoCoder.newInstance();
    private List<OrderAddress> oas;
    private RecyclerView rv;
    private AddrAdapter adapter;
    private List<PoiInfo> poiList;
    private EditText search_edit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        MapUtil.registerMyLocation(lockMeEnd);
        mSearch.setOnGetGeoCodeResultListener(listener);
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, LocationActivity.this)) {
            MapUtil.startLockMe();
        } else {
            PermissionUtil.applyFineLocation(LocationActivity.this);
        }

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapUtil.unRegisterMyLocation(lockMeEnd);
        mPoiSearch.destroy();
        mSearch.destroy();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.add_address:
                if (RuntimeConfig.userValid()) {
                    Intent intent = new Intent(LocationActivity.this, OrderAddressEditActivity.class);
                    intent.putExtra(Constant.Key.KEY_LOCATION_CHOSE, choseLocation);
                    startActivity(intent);
                } else {
                    Intent login = new Intent(LocationActivity.this, LoginActivity.class);
                    startActivity(login);
                }
                finish();
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.FINE_LOCATION_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    //用户成功授权
                    if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, LocationActivity.this)) {
                        MapUtil.startLockMe();

                    } else {
                        ToastUtil.toast(this, "缺少位置权限,可能会影响定位");
                        PermissionUtil.goPermissionManager(LocationActivity.this);
                        finish();
                        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                    }
                } else {
                    //用户拒绝授权
                    ToastUtil.toast(this, "缺少位置权限,可能会影响定位");
                    PermissionUtil.goPermissionManager(LocationActivity.this);
                    finish();
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
                break;
            default:
                ToastUtil.toast(requestCode + "");
                break;
        }
    }

    private void initView() {
        rv = findViewById(R.id.rv);
        search_edit = findViewById(R.id.search_edit);

        LinearLayoutManager m = new LinearLayoutManager(LocationActivity.this);
        m.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(m);
        adapter = new AddrAdapter();
        rv.setAdapter(adapter);

        //配合布局在软键盘enter位置显示【搜索】
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    mPoiSearch.searchNearby(new PoiNearbySearchOption()
                            .keyword(textView.getText().toString().trim())
                            .sortType(PoiSortType.comprehensive)
                            .location(RuntimeConfig.myLocation)
                            .radius(1000000)
                            .pageNum(1));
                    return true;
                }
                return false;
            }
        });
    }


    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ADDRESS_LIST)
                        , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                .getParams());

                final OrderAddressResult result = GsonUtil.fromJson(rs, OrderAddressResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    oas = result.getData();
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }


    private class AddrAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new SendToVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_location_sendto, parent, false));
            } else if (viewType == 1) {
                //标题
                return new TitleVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_location_title, parent, false));
            } else if (viewType == 2) {
                //收货地址
                return new OAVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_location_address, parent, false));
            } else if (viewType == 3) {
                //附近地址
                return new NearbyVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_location_nearby, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SendToVH) {
                ((SendToVH) holder).setData();
            } else if (holder instanceof TitleVH) {
                ((TitleVH) holder).setData(position);
            } else if (holder instanceof OAVH) {
                ((OAVH) holder).setData(oas.get(position - 2));
            } else if (holder instanceof NearbyVH) {
                ((NearbyVH) holder).setData(position - (oas == null ? 0 : oas.size()) - 3);
            }
        }

        @Override
        public int getItemCount() {
            return 1 +
                    1 +
                    (oas == null ? 0 : oas.size()) +
                    1 +
                    (poiList == null ? 0 : poiList.size());
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else if (position == 1) {
                return 1;
            } else if (position < (oas == null ? 0 : oas.size()) + 2) {
                return 2;
            } else if (position == (oas == null ? 0 : oas.size()) + 2) {
                return 1;
            } else {
                return 3;
            }
        }
    }

    /**
     * 收货地址 VH
     */
    private class OAVH extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView phone;
        private final TextView addres;

        OAVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            addres = itemView.findViewById(R.id.address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RuntimeConfig.USER_ADDRESS = addres.getText().toString().trim();
                    finish();
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        }

        public void setData(final OrderAddress address) {
            name.setText(address.getPerson());
            phone.setText(address.getPhone());
            addres.setText(address.getProvince() + address.getCity() + address.getDistrict() + address.getAddress());
        }
    }

    private class SendToVH extends RecyclerView.ViewHolder {
        private final TextView textView;

        SendToVH(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.sendto);
        }

        public void setData() {
            textView.setText(" 送至：" + RuntimeConfig.USER_ADDRESS);

        }
    }

    private class TitleVH extends RecyclerView.ViewHolder {

        private final TextView textView;

        TitleVH(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
        }

        public void setData(int pos) {
            if (pos == 0) {
                textView.setText("送至");
            } else if (pos == 1) {
                textView.setText("我的收货地址");
            } else if (pos > 1) {
                textView.setText("附近地址");
            }
        }
    }

    private class NearbyVH extends RecyclerView.ViewHolder {

        private final TextView txt;
        private final TextView reLock;
        private int pos;

        NearbyVH(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.nearby);
            reLock = itemView.findViewById(R.id.reLock);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RuntimeConfig.USER_ADDRESS = txt.getText().toString().trim();
                    adapter.notifyDataSetChanged();
                    choseLocation = poiList.get(pos).location;
                    rv.scrollToPosition(0);
                }
            });
            reLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, LocationActivity.this)) {
                        MapUtil.startLockMe();
                    } else {
                        PermissionUtil.applyFineLocation(LocationActivity.this);
                    }

                }
            });
        }

        public void setData(int p) {
            pos = p;
            txt.setText(poiList.get(p).address);
            if (p == 0) {
                reLock.setVisibility(View.VISIBLE);
            } else {
                reLock.setVisibility(View.GONE);
            }
        }
    }
}
