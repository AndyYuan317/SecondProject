package com.wingplus.coomohome.web;

/**
 * 商品简介
 * @author leaffun.
 *         Create on 2017/9/25.
 */
public class GoodIntro {

    /**
     * 方图
     */
    private int squareImg;

    /**
     * 活动图
     */
    private int promotionImg;

    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 商品介绍（简介 或者 属性拼接：分两个字段好些）
     */
    private String brief;

    /**
     * 商品属性
     */
    private String attrs;

    /**
     * 价格
     */
    private double price;

    /**
     * 原价
     */
    private double orgPrice;

    /**
     * 活动Tag
     */
    private String promotionName;

    /**
     * 商品sku
     */
    private String sku;

    /**
     * 商品数量
     */
    private int num;

    public int getSquareImg() {
        return squareImg;
    }

    public void setSquareImg(int squareImg) {
        this.squareImg = squareImg;
    }

    public int getPromotionImg() {
        return promotionImg;
    }

    public void setPromotionImg(int promotionImg) {
        this.promotionImg = promotionImg;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOrgPrice() {
        return orgPrice;
    }

    public void setOrgPrice(double orgPrice) {
        this.orgPrice = orgPrice;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
