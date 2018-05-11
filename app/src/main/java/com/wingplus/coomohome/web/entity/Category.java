package com.wingplus.coomohome.web.entity;

/**
 * 分类
 * @author leaffun.
 *         Create on 2017/9/26.
 */
public class Category {

    /**
     * 分类编号
     */
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图片链接
     */
    private String imgUrl;

    /**
     * 分类排序
     */
    private int order;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
