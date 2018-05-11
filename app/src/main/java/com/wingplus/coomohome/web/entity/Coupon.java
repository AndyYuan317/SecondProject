package com.wingplus.coomohome.web.entity;

/**
 * 优惠券
 *
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public class Coupon {
    /**
     * 优惠券id
     */
    private long id;

    /**
     * 优惠券金额(元)
     */
    private double price;

    /**
     * 优惠说明
     */
    private String description;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 优惠券状态
     */
    private int state;

    /**
     * 有效使用范围说明 X
     */
    private String validRemark;

    /**
     * 使用说明 X
     */
    private String TimeRemark;

    /**
     * 优惠券类型0：抵扣金额；1：首件打折
     */
    private int bonusType;

    /**
     * 日期范围类型 ：0、固定日期区间；1、固定有效天数
     */
    private int dateType;

    /**
     * 有效天数
     */
    private int dateRange;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValidRemark() {
        return validRemark;
    }

    public void setValidRemark(String validRemark) {
        this.validRemark = validRemark;
    }

    public String getTimeRemark() {
        return TimeRemark;
    }

    public void setTimeRemark(String timeRemark) {
        TimeRemark = timeRemark;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getBonusType() {
        return bonusType;
    }

    public void setBonusType(int bonusType) {
        this.bonusType = bonusType;
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }

    public int getDateRange() {
        return dateRange;
    }

    public void setDateRange(int dateRange) {
        this.dateRange = dateRange;
    }
}
