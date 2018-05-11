package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 拆单的子订单信息
 *
 * @author leaffun.
 *         Create on 2017/11/7.
 */
public class SubOrder {
    /**
     * 订单id
     */
    private long id;
    /**
     * 发货仓名称
     */
    private String wareName;
    /**
     * 发货仓id
     */
    private int depotId;
    /**
     * 配送费用
     */
    private double shipping;
    /**
     * 订单状态
     */
    private int status;
    /**
     * 应付金额
     */
    private double payment;
    /**
     * 优惠券
     */
    private double coupon;
    /**
     * 优惠活动金额
     */
    private double activity;
    /**
     * 积分？？？ // todo 暂无积分抵扣
     */
    private double score;
    /**
     * 订单商品
     */
    private List<GoodShow> goods;

    /**
     * 订单编号
     */
    private String orderCode;
    /**
     * 下单时间
     */
    private String orderTime;
    /**
     * 支付方式
     */
    private String payType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    public int getDepotId() {
        return depotId;
    }

    public void setDepotId(int depotId) {
        this.depotId = depotId;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<GoodShow> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodShow> goods) {
        this.goods = goods;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
