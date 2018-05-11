package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 订单
 * @author leaffun.
 *         Create on 2017/10/10.
 */
public class Order {

    /**
     * 订单id
     */
    private long id;

    /**
     * 订单编号
     */
    private String code;

    /**
     * 订单状态
     */
    private int state;

    /**
     * 订单商品
     */
    private List<GoodShow> goods;

    /**
     * 送达地址
     */
    private OrderAddress orderAddress;

    /**
     * 下单时间
     */
    private long orderTime;

    /**
     * 支付方式
     */
    private String payment;

    /**
     * 商品合计
     */
    private double goodCount;

    /**
     * 运费
     */
    private double freight;

    /**
     * 优惠
     */
    private double discount;

    /**
     * 实付
     */
    private double payAmount;

    /**
     * 应付（显示在外的总额）
     */
    private double needPayAmout;

    /**
     * 是否评价过（0：否，1：是）
     */
    private int isCommented;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<GoodShow> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodShow> goods) {
        this.goods = goods;
    }

    public OrderAddress getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(OrderAddress orderAddress) {
        this.orderAddress = orderAddress;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public double getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(double goodCount) {
        this.goodCount = goodCount;
    }

    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public double getNeedPayAmout() {
        return needPayAmout;
    }

    public void setNeedPayAmout(double needPayAmout) {
        this.needPayAmout = needPayAmout;
    }

    public int getIsCommented() {
        return isCommented;
    }

    public void setIsCommented(int isCommented) {
        this.isCommented = isCommented;
    }
}
