package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 活动信息
 * @author leaffun.
 *         Create on 2017/10/23.
 */
public class ActivityInfo {

    private long id;

    private String name;

    private List<WebBanner> banner;

    private List<GoodShow> goods;

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

    public List<WebBanner> getBanner() {
        return banner;
    }

    public void setBanner(List<WebBanner> banner) {
        this.banner = banner;
    }

    public List<GoodShow> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodShow> goods) {
        this.goods = goods;
    }
}
