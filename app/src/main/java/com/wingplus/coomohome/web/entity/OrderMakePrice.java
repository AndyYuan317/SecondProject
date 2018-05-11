package com.wingplus.coomohome.web.entity;

/**
 * 订单确认-订单总价格信息
 * @author leaffun.
 *         Create on 2017/11/7.
 */
public class OrderMakePrice {
    private double total;
    private double shipping;
    private double coupon;
    private double score;
    private double activity;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }
}
