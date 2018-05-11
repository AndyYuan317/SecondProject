package com.wingplus.coomohome.web.entity;

import java.io.Serializable;

/**
 * 可退货商品
 * @author leaffun.
 *         Create on 2017/11/19.
 */
public class AfterSaleValid implements Serializable{

    /**
     * orderId : 65
     * orderSn : 151063199837
     * orderDate : 2017-11-14 11:59:58
     * orderStatus : 5
     * itemId : 109
     * goodsId : 282
     * productId : 373
     * catId : 109
     * num : 1
     * goodsSn : 201511240084
     * goodsImg : http://static.v5.javamall.com.cn/attachment/goods/201511241204330677_thumbnail.jpg
     * name : 云思木想2015秋冬新款修身女装毛呢外套纯黑时尚中长款外套71355
     * price : 0.0
     *
     * id
     * date
     * code
     * state
     * imgUrl
     * amount
     */

    private int orderId;
    private String orderSn;
    private String orderDate;
    private int orderStatus;
    private int itemId;
    private int goodsId;
    private int productId;
    private int catId;
    private int num;
    private String goodsSn;
    private String goodsImg;
    private String name;
    private double price;
    private String spec;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }
}
