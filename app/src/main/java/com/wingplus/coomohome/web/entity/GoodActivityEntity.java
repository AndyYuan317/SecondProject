package com.wingplus.coomohome.web.entity;

/**
 * 商品活动单体信息
 *
 * @author leaffun.
 *         Create on 2017/11/2.
 */
public class GoodActivityEntity {
    private long id;
    private String name;
    private String tag;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
