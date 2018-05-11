package com.wingplus.coomohome.web.entity;

import com.wingplus.coomohome.web.CommitEntity;

import java.util.List;

/**
 * 评论提交的json参数
 * @author leaffun.
 *         Create on 2017/11/17.
 */
public class CommitJson {

    /**
     * id :
     * orderId : 1
     * date : 2017-11-16
     * name :
     * headImg :
     * level :
     * anonymous : 0
     * deptScore : 5
     * serviceScore : 5
     * shippingScore : 5
     * goodsEvalList : [{"id":1,"goodsId":1,"productId":1,"comment":"评价","imgUrl":["图片地址"],"score":0}]
     */

    private String id;
    private long orderId;
    private String date;
    private String name;
    private String headImg;
    private String level;
    private int anonymous;
    private int deptScore;
    private int serviceScore;
    private int shippingScore;
    private List<CommitEntity> goodsEvalList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(int anonymous) {
        this.anonymous = anonymous;
    }

    public int getDeptScore() {
        return deptScore;
    }

    public void setDeptScore(int deptScore) {
        this.deptScore = deptScore;
    }

    public int getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(int serviceScore) {
        this.serviceScore = serviceScore;
    }

    public int getShippingScore() {
        return shippingScore;
    }

    public void setShippingScore(int shippingScore) {
        this.shippingScore = shippingScore;
    }

    public List<CommitEntity> getGoodsEvalList() {
        return goodsEvalList;
    }

    public void setGoodsEvalList(List<CommitEntity> goodsEvalList) {
        this.goodsEvalList = goodsEvalList;
    }

}
