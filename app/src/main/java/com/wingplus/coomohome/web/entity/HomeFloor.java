package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 首页楼层
 * @author leaffun.
 *         Create on 2017/10/21.
 */
public class HomeFloor {
    private String name;

    private WebBanner topic;

    private List<GoodShow> goods;

    private int order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WebBanner getTopic() {
        return topic;
    }

    public void setTopic(WebBanner topic) {
        this.topic = topic;
    }

    public List<GoodShow> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodShow> goods) {
        this.goods = goods;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
