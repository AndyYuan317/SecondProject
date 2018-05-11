package com.wingplus.coomohome.web.entity;

import com.baidu.mapapi.model.LatLng;

/**
 * 门店
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class Store {

    /**
     * id
     */
    private long id;

    /**
     * 仓库id
     */
    private long depot_id;

    /**
     * 门店图片
     */
    private String cover;

    /**
     * 店名
     */
    private String name;

    /**
     * 地址
     */
    private String location;

    /**
     * 距离
     */
    private String distance;

    /**
     * 存储查询到的latLng
     */
    private LatLng latLng;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDepot_id() {
        return depot_id;
    }

    public void setDepot_id(long depot_id) {
        this.depot_id = depot_id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
