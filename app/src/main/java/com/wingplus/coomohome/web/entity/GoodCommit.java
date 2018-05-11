package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 商品评价
 *
 * @author leaffun.
 *         Create on 2017/11/13.
 */
public class GoodCommit {
    /**
     * 评价内容
     */
    private String comment;
    /**
     * 评价时间
     */
    private String date;

    /**
     * 评价人名称
     */
    private String name;

    /**
     * 评价人头像
     */
    private String headImg;

    /**
     * 评价人等级
     */
    private String level;

    /**
     * 评价图片
     */
    private List<String> imgUrl;

    /**
     * 是否匿名
     */
    private String anonymous;

    /**
     * 综合得分
     */
    private int score;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
