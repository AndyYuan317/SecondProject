package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 物流信息
 * @author leaffun.
 *         Create on 2017/11/13.
 */
public class LogisticsInfo {
    private String message;
    private String nu;
    private int ischeck;
    private String condition;
    private String com;
    private String status;
    private String state;
    private List<LogisticsEntity> data;
    private String logiName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public int getIscheck() {
        return ischeck;
    }

    public void setIscheck(int ischeck) {
        this.ischeck = ischeck;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<LogisticsEntity> getData() {
        return data;
    }

    public void setData(List<LogisticsEntity> data) {
        this.data = data;
    }

    public String getLogiName() {
        return logiName;
    }

    public void setLogiName(String logiName) {
        this.logiName = logiName;
    }
}
