package com.wingplus.coomohome.web.entity;

/**
 * 配送方式
 *
 * @author leaffun.
 *         Create on 2017/11/8.
 */
public class Shipping {
    private long id;
    private String name;
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
