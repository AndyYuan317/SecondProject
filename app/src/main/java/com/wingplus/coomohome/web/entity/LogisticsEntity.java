package com.wingplus.coomohome.web.entity;

/**
 * 物流阶段信息
 *
 * @author leaffun.
 *         Create on 2017/11/13.
 */
public class LogisticsEntity {
    private String time;
    private String ftime;
    private String context;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFtime() {
        return ftime;
    }

    public void setFtime(String ftime) {
        this.ftime = ftime;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
