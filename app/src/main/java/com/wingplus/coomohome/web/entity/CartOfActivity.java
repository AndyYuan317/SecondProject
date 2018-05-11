package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 购物车单个活动下的商品集合
 *
 * @author leaffun.
 *         Create on 2017/11/3.
 */
public class CartOfActivity {

    /**
     * 活动id
     */
    private long id;

    /**
     * 活动类型（普通活动，优惠组合）
     */
    private int type;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动标签
     */
    private String tag;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 商品集合
     */
    private List<GoodShow> goods;

    /**
     * 商品总活动价
     */
    private double price;

    /**
     * 商品总原价
     */
    private double originalPrice;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GoodShow> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodShow> goods) {
        this.goods = goods;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
}
