package com.wingplus.coomohome.web.entity;

/**
 * 充值记录
 *
 * @author leaffun.
 *         Create on 2017/10/10.
 */
public class RechargeRecord {

    private long id;

    private String date;

    private String amount;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
