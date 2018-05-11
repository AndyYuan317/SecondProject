package com.wingplus.coomohome.web.entity;

/**
 * 订单计数
 *
 * @author leaffun.
 *         Create on 2017/12/14.
 */
public class OrderCnt {

    private int notCommented;
    private int shipped;
    private int notPayed;
    private int payed;

    public int getNotCommented() {
        return notCommented;
    }

    public void setNotCommented(int notCommented) {
        this.notCommented = notCommented;
    }

    public int getShipped() {
        return shipped;
    }

    public void setShipped(int shipped) {
        this.shipped = shipped;
    }

    public int getNotPayed() {
        return notPayed;
    }

    public void setNotPayed(int notPayed) {
        this.notPayed = notPayed;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
        this.payed = payed;
    }
}
