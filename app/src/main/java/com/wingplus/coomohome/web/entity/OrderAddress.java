package com.wingplus.coomohome.web.entity;

import java.io.Serializable;

/**
 * 收货地址
 *
 * @author leaffun.
 *         Create on 2017/10/11.
 */
public class OrderAddress implements Serializable {

    /**
     * 地址id
     */
    private long id;

    /**
     * 收货人
     */
    private String person;

    /**
     * 收货人手机号
     */
    private String phone;

    /**
     * 省份
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县/区
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 是否默认
     * 0：否 ,1：是
     */
    private int isDefault;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
