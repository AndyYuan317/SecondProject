package com.wingplus.coomohome.activity;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.citychoose.XmlParserHandler;
import com.wingplus.coomohome.component.citychoose.model.CityModel;
import com.wingplus.coomohome.component.citychoose.model.DistrictModel;
import com.wingplus.coomohome.component.citychoose.model.ProvinceModel;
import com.wingplus.coomohome.component.citychoose.view.SelectAddressDialog;
import com.wingplus.coomohome.component.citychoose.view.myinterface.SelectAddressInterface;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.CheckUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.MapUtil;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 地址编辑
 *
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class OrderAddressEditActivity extends BaseActivity implements View.OnClickListener {

    private TextView province;
    private EditText addressDetail;
    private EditText person;
    private EditText phone;
    private CheckBox checkBox;

    private boolean newAddress;
    private OrderAddress orderAddress;

    /**
     * 选中的地址，用-分割省-市-县
     */
    private String chooseProvince;

    private GeoCoder mSearch = GeoCoder.newInstance();
    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
            }
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            MallApplication.showProgressDialog(false, OrderAddressEditActivity.this);
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果

            } else {
                ReverseGeoCodeResult.AddressComponent ad = result.getAddressDetail();
                if (ad != null) {
                    String provinceTxt = ad.province;
                    String cityTxt = ad.city;
                    String districtTxt = ad.district;
                    String address = result.getAddress();
                    chooseProvince = matchAddress(provinceTxt, cityTxt, districtTxt);
                    province.setText(chooseProvince.replace("-", ""));
                    addressDetail.setText(address);
                }
            }
        }
    };
    MapUtil.LockMeEnd lockMeEnd = new MapUtil.LockMeEnd() {
        @Override
        public void useMyLocation() {
            MallApplication.showProgressDialog(false, OrderAddressEditActivity.this);
            getAddressByLocation(RuntimeConfig.myLocation);
        }
    };


    private String matchAddress(String province, String city, String district) {
        List<ProvinceModel> addressData = getAddressData();
        if (addressData == null || addressData.size() <= 0) return "";

        if (province.length() > 1 && (province.endsWith("省") || province.endsWith("市"))) {
            province = province.substring(0, province.length() - 1);
        }
        ProvinceModel likeProvince = null;
        for (ProvinceModel pm : addressData) {
            if (pm.getName().contains(province)) {
                likeProvince = pm;
                break;
            }
        }
        if (likeProvince == null) return "";

        List<CityModel> cityList = likeProvince.getCityList();
        if (cityList == null || cityList.size() <= 0) return "";
        if (city.length() > 1 && city.endsWith("市")) {
            city = city.substring(0, city.length() - 1);
        }
        CityModel likeCity = null;
        for (CityModel cm : cityList) {
            if (cm.getName().contains(city)) {
                likeCity = cm;
                break;
            }
        }
        if (likeCity == null) return "";

        List<DistrictModel> districtList = likeCity.getDistrictList();
        if (districtList == null | districtList.size() <= 0) return "";
        if (district.length() > 1 && (district.endsWith("县") || district.endsWith("区"))) {
            district = district.substring(0, district.length() - 1);
        }
        DistrictModel likeDistrict = null;
        for (DistrictModel dm : districtList) {
            if (dm.getName().contains(district)) {
                likeDistrict = dm;
                break;
            }
        }
        if (likeDistrict == null) return "";
        return likeProvince.getName() + "-" + likeCity.getName() + "-" + likeDistrict.getName();
    }

    private List<ProvinceModel> getAddressData() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = this.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return provinceList;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        MapUtil.registerMyLocation(lockMeEnd);
        mSearch.setOnGetGeoCodeResultListener(listener);
        initView();
        orderAddress = (OrderAddress) getIntent().getSerializableExtra(Constant.Key.KEY_ORDER_ADDRESS);
        newAddress = (orderAddress == null);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapUtil.unRegisterMyLocation(lockMeEnd);
        mSearch.destroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.set_default:
                checkBox.setChecked(!checkBox.isChecked());
                break;
            case R.id.save:
                saveAddress();
                break;
            case R.id.address:

                SelectAddressDialog selectAddressDialog = new SelectAddressDialog(this, new SelectAddressInterface() {
                    @Override
                    public void setAreaString(String area) {
                        chooseProvince = area;
                        province.setText(chooseProvince.replaceAll("-", ""));
                    }

                    @Override
                    public void setTime(String time) {

                    }

                    @Override
                    public void setGender(String gender) {

                    }
                }, SelectAddressDialog.STYLE_THREE, null, orderAddress);
                selectAddressDialog.showDialog();

                break;
            case R.id.address_detail:
                addressDetail.setCursorVisible(true);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.FINE_LOCATION_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    //用户成功授权
                    if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, OrderAddressEditActivity.this)) {
                        MallApplication.showProgressDialog(true, OrderAddressEditActivity.this);
                        MapUtil.startLockMe();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        province = findViewById(R.id.address);
        addressDetail = findViewById(R.id.address_detail);
        person = findViewById(R.id.name);
        phone = findViewById(R.id.phone);

        checkBox = findViewById(R.id.checkbox);
        LinearLayout setDefault = findViewById(R.id.set_default);
        setDefault.setOnClickListener(this);

        addressDetail.setCursorVisible(false);
//        addressDetail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                person.requestFocus();
//                return true;
//            }
//        });
    }

    private void initData() {
        if (orderAddress != null) {
            province.setText(orderAddress.getProvince() + orderAddress.getCity() + orderAddress.getDistrict());
            addressDetail.setText(orderAddress.getAddress());
            person.setText(orderAddress.getPerson());
            phone.setText(orderAddress.getPhone());
            checkBox.setChecked(orderAddress.getIsDefault() == Constant.OrderAddress.IS_DEFAULT);
            chooseProvince = orderAddress.getProvince() + "-" + orderAddress.getCity() + "-" + orderAddress.getDistrict();
        } else {
            orderAddress = new OrderAddress();
            LatLng location = getIntent().getParcelableExtra(Constant.Key.KEY_LOCATION_CHOSE);
            if (location != null) {
                getAddressByLocation(location);
            } else {
                // 开始定位
                if (PermissionUtil.hasPermission(PermissionUtil.FINE_LOCATION_STRING, OrderAddressEditActivity.this)) {
                    MallApplication.showProgressDialog(true, OrderAddressEditActivity.this);
                    MapUtil.startLockMe();
                } else {
                    PermissionUtil.applyFineLocation(OrderAddressEditActivity.this);
                }
            }

        }
    }

    private void getAddressByLocation(LatLng location) {
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(location));
        MallApplication.showProgressDialog(true, OrderAddressEditActivity.this);
    }


    /**
     * 保存地址
     *
     * @return
     */
    private void saveAddress() {
        if (province.getText().toString().trim().length() == 0) {
            ToastUtil.toast(getApplicationContext(), "请选择省份、城市、县区");
            return;
        } else if (addressDetail.getText().toString().trim().length() == 0) {
            ToastUtil.toast(getApplicationContext(), "请填写详细地址");
            return;
        } else if (person.getText().toString().trim().length() == 0) {
            ToastUtil.toast(getApplicationContext(), "请填写姓名");
            return;
        } else if (!CheckUtil.isMobileNO(phone.getText().toString().trim())) {
            ToastUtil.toast(getApplicationContext(), "请填写正确的手机号");
            return;
        }


        if (newAddress) {
            //新增
            orderAddress = new OrderAddress();
        }

        String[] split = chooseProvince.split("-");
        orderAddress.setProvince(split[0]);
        orderAddress.setCity(split[1]);
        orderAddress.setDistrict(split[2]);

        orderAddress.setAddress(addressDetail.getText().toString().trim());
        orderAddress.setPerson(person.getText().toString().trim());
        orderAddress.setPhone(phone.getText().toString().trim());
        orderAddress.setIsDefault(checkBox.isChecked() ? Constant.OrderAddress.IS_DEFAULT : Constant.OrderAddress.NOT_DEFAULT);


        final ParamsBuilder paramsBuilder = new ParamsBuilder()
                .addParam("token", RuntimeConfig.user.getToken())
                .addParam("person", orderAddress.getPerson())
                .addParam("phone", orderAddress.getPhone())
                .addParam("province", orderAddress.getProvince())
                .addParam("city", orderAddress.getCity())
                .addParam("district", orderAddress.getDistrict())
                .addParam("address", orderAddress.getAddress())
                .addParam("isDefault", "" + orderAddress.getIsDefault());
        if (!newAddress) {
            paramsBuilder.addParam("addressId", "" + orderAddress.getId());
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(newAddress ? APIConfig.API_ADDRESS_ADD : APIConfig.API_ADDRESS_MODIFY)
                        , paramsBuilder.getParams());
                BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    ToastUtil.toast(getApplicationContext(), "保存成功");
                    finish();
                    overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });
    }
}
