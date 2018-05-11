package com.wingplus.coomohome.web.entity;

/**
 * 消息
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class Welfare {

    private long activity_id;


    /**
     * 标题
     */
   private String activity_name;


    /**
     * 发布时间(无)
     */
    private long time;

    /**
     * 图片
     */
    private String activity_img;

    /**
     * 描述
     */
    private String activity_desp;

    /**
     * 类型，0：活动(带图片)，1：公告
     */
    private int type;

    public long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(long activity_id) {
        this.activity_id = activity_id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getActivity_img() {
        return activity_img;
    }

    public void setActivity_img(String activity_img) {
        this.activity_img = activity_img;
    }

    public String getActivity_desp() {
        return activity_desp;
    }

    public void setActivity_desp(String activity_desp) {
        this.activity_desp = activity_desp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
