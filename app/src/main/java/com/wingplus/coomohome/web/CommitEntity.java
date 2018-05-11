package com.wingplus.coomohome.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 收集评价使用的Entity
 *
 * @author leaffun.
 *         Create on 2017/11/13.
 */
public class CommitEntity {
    /**
     * 评价id
     */
    private String id;
    /**
     * 商品id
     */
    private String goodsId;
    /**
     * 规格id
     */
    private String productId;
    /**
     * 商品首图
     */
    private String goodImg;
    /**
     * 商品得分
     */
    private int score;
    /**
     * 评价
     */
    private String comment;
    /**
     * 评论图片
     */
    private List<String> imgUrl;


    public CommitEntity() {
        this.imgUrl = new ArrayList<>();
        this.score = 5;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getGoodImg() {
        return goodImg;
    }

    public void setGoodImg(String goodImg) {
        this.goodImg = goodImg;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }
}
