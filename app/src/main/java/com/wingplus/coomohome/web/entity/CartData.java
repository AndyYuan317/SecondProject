package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 购物车商品集合
 *
 * @author leaffun.
 *         Create on 2017/11/3.
 */
public class CartData {
    /**
     * 无活动
     */
    private List<GoodShow> common;

    /**
     * 优惠活动
     */
    private List<CartOfActivity> activity;

    /**
     * 购物车总数量
     */
    private int cnt;

    /**
     * 购物车总价
     */
    private double total;

    public List<GoodShow> getCommon() {
        return common;
    }

    public void setCommon(List<GoodShow> common) {
        this.common = common;
    }

    public List<CartOfActivity> getActivity() {
        return activity;
    }

    public void setActivity(List<CartOfActivity> activity) {
        this.activity = activity;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
