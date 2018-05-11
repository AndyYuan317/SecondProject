package com.wingplus.coomohome.web;

/**
 * 消息
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class News {
    /**
     * 标题
     */
   private String title;

    /**
     * 发布时间
     */
    private long time;

    /**
     * 图片
     */
    private String img;

    /**
     * 描述
     */
    private String des;

    /**
     * 类型，0：活动(带图片)，1：公告
     */
    private int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
