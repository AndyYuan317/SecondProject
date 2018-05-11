package com.wingplus.coomohome.web.entity;

/**
 * 轮播图
 * @author leaffun.
 *         Create on 2017/8/29.
 */
public class WebBanner {

    /**
     * 名称
     */
    private String name;

    /**
     * 类型（0:纯图片,不能跳转; 1:跳转到商品详情页; 2:跳转到活动详情页; 3:跳转到商品列表页; 4:跳转到外部网页;）
     */
    private int type;

    /**
     * 图片路径
     */
    private String imgUrl;

    /**
     * 响应内容(跳转到详情的参数，配合type进行页面跳转。type=0:空; type=1:商品ID; type=2:活动ID; type=3:商品分类ID; type=4:外部url;)
     */
    private String params;

    public WebBanner(String img, String url){
        this.imgUrl = img;
        this.params = url;
    }

    public String getImgUrl(){
        return imgUrl;
    }

    public void setImgUrl(String imgUrl){
        this.imgUrl = imgUrl;
    }

    public String getParams(){
        return params;
    }

    public void setParams(String params){
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
