package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 商品展列
 *
 * @author leaffun.
 *         Create on 2017/10/19.
 */
public class GoodShow {
    /**
     * 商品编号
     */
    private long id;

    /**
     * 商品货号（不同规格）
     */
    private String productId;

    /**
     * 库存量
     */
    private int stock;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片组
     */
    private List<String> imgUrl;

    /**
     * 商品介绍
     */
    private String description;

    /**
     * 商品属性（空格隔开）
     */
    private String spec;

    /**
     * 商品现价
     */
    private double price;

    /**
     * 商品原价
     */
    private double originalPrice;

    /**
     * 编号
     */
    private int order;

    /**
     * 商品标签组
     */
    private List<String> tag;

    /**
     * 在购物车中是否勾选
     */
    private int isChecked;

    /**
     * # 商品选中：我的收藏编辑时用到
     */
    private boolean isChose;

    /**
     * # 商品数量：订单展示时用到, 在购物车用到
     */
    private int num;

    /**
     * 购物车中的编号
     */
    private long cartItemId;

    private int isNew;

    /**
     * 产地
     */
    private String place;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChose() {
        return isChose;
    }

    public void setChose(boolean chose) {
        this.isChose = chose;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
