package com.wingplus.coomohome.web.entity;

/**
 * @author leaffun.
 *         Create on 2017/11/30.
 */
public class DlyCenter {

    /**
     * dly_center_id : 1
     * name : 上海总仓
     * address : 吴中路686弄2号E座1308
     * province : 上海市
     * city : 上海市
     * region : 闵行区
     * zip : 200000
     * uname : 王亚南
     * cellphone : 13764275155
     */

    private int dly_center_id;
    private String name;
    private String address;
    private String province;
    private String city;
    private String region;
    private String zip;
    private String uname;
    private String cellphone;

    public int getDly_center_id() {
        return dly_center_id;
    }

    public void setDly_center_id(int dly_center_id) {
        this.dly_center_id = dly_center_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
}

