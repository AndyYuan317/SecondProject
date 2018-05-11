package com.wingplus.coomohome.web.entity;

/**
 * （提交）确认订单结果
 *
 * @author leaffun.
 *         Create on 2017/11/9.
 */
public class OrderApply {
    private double amount;
    private long orderId;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
