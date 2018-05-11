package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 订单确认信息
 * @author leaffun.
 *         Create on 2017/11/7.
 */
public class OrderMake {
    /**
     * 订单id
     */
    private long id;
    /**
     * 配送方式
     */
    private Shipping shipping;
    /**
     * 地址Entity
     */
    private OrderAddress address;
    /**
     * 优惠券Entity
     */
    private Coupon coupon;

    /**
     * 发票信息
     */
    private Receipt receipt;

    /**
     * 支付统计明细
     */
    private OrderMakePrice price;
    /**
     * 应付
     */
    private double payment;
    /**
     * 订单详细
     */
    private List<SubOrder> subOrders;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public OrderAddress getAddress() {
        return address;
    }

    public void setAddress(OrderAddress address) {
        this.address = address;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public OrderMakePrice getPrice() {
        return price;
    }

    public void setPrice(OrderMakePrice price) {
        this.price = price;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public List<SubOrder> getSubOrders() {
        return subOrders;
    }

    public void setSubOrders(List<SubOrder> subOrders) {
        this.subOrders = subOrders;
    }
}
