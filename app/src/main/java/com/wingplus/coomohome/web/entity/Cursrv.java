package com.wingplus.coomohome.web.entity;

/**
 * 客服人员信息
 * @author leaffun.
 *         Create on 2017/11/30.
 */
public class Cursrv {


    private int cus_srv_id;
    private String cus_srv_name;
    private String qq;
    private int sort;
    private int srv_count;
    private int is_current;

    public int getCus_srv_id() {
        return cus_srv_id;
    }

    public void setCus_srv_id(int cus_srv_id) {
        this.cus_srv_id = cus_srv_id;
    }

    public String getCus_srv_name() {
        return cus_srv_name;
    }

    public void setCus_srv_name(String cus_srv_name) {
        this.cus_srv_name = cus_srv_name;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSrv_count() {
        return srv_count;
    }

    public void setSrv_count(int srv_count) {
        this.srv_count = srv_count;
    }

    public int getIs_current() {
        return is_current;
    }

    public void setIs_current(int is_current) {
        this.is_current = is_current;
    }
}
