package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 售后单提交后结果
 * @author leaffun.
 *         Create on 2017/11/19.
 */
public class AfterSaleOrder {

    /**
     * id : 4
     * date : 1511098302
     * code : 171119093142
     * state : 0
     * imgUrl : ["http://106.14.80.60:8080/b2c/statics/attachment/aftersale/2017/11/19/21//30208724.jpg"]
     * amount : 360.0
     * orderCode : 151053773532
     * reason : 收到商品与描述不符
     * detail : 商品不符合描述
     * person : Android
     * phone : 18899871111
     * payedAmount : 360.0
     * shippingType : 0
     * express : null
     * expressName : null
     * expressNo : null
     * wareHouse : null
     * wareHouseName : null
     * deliverDate : null
     * "goodsPhoto": "http://static.v5.javamall.com.cn/attachment/goods/201511241157540387_thumbnail.jpg",
     "goodsName": "sofa2015秋冬新款纯色不对称宽松版型九分袖套头针织衫 ",
     "goodsSpec": "西瓜红",
     "goodsNum": 1
     */

    private long id;
    private String date;
    private String code;
    private int state;
    private double amount;
    private String orderCode;
    private String reason;
    private String detail;
    private String person;
    private String phone;
    private double payedAmount;
    private int shippingType;
    private String express;
    private String expressName;
    private String expressNo;
    private String wareHouse;
    private String wareHouseName;
    private String deliverDate;
    private List<String> imgUrl;
    private String goodsPhoto;
    private String goodsName;
    private String goodsSpec;
    private int goodsNum;
    private double goodsPrice;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public double getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(double payedAmount) {
        this.payedAmount = payedAmount;
    }

    public int getShippingType() {
        return shippingType;
    }

    public void setShippingType(int shippingType) {
        this.shippingType = shippingType;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(String wareHouse) {
        this.wareHouse = wareHouse;
    }

    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public String getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(String deliverDate) {
        this.deliverDate = deliverDate;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGoodsPhoto() {
        return goodsPhoto;
    }

    public void setGoodsPhoto(String goodsPhoto) {
        this.goodsPhoto = goodsPhoto;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSpec() {
        return goodsSpec;
    }

    public void setGoodsSpec(String goodsSpec) {
        this.goodsSpec = goodsSpec;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
}
