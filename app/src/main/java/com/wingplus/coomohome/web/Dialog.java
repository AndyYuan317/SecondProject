package com.wingplus.coomohome.web;

/**
 * 对话
 * @author leaffun.
 *         Create on 2017/10/14.
 */
public class Dialog {

    /**
     * 发送时间
     */
    private long sendTime;


    /**
     * 客服编号名称
     */
    private String staff;

    /**
     * 客服头像
     */
    private String staffImage;

    /**
     * 客服消息
     */
    private String staffNews;


    /**
     * 用户名称
     */
    private String user;

    /**
     * 用户头像
     */
    private String userImage;

    /**
     * 用户消息
     */
    private String userNews;

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getStaffImage() {
        return staffImage;
    }

    public void setStaffImage(String staffImage) {
        this.staffImage = staffImage;
    }

    public String getStaffNews() {
        return staffNews;
    }

    public void setStaffNews(String staffNews) {
        this.staffNews = staffNews;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserNews() {
        return userNews;
    }

    public void setUserNews(String userNews) {
        this.userNews = userNews;
    }
}
