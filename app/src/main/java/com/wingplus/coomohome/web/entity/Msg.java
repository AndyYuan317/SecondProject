package com.wingplus.coomohome.web.entity;

/**
 * 消息
 * @author leaffun.
 *         Create on 2017/10/28.
 */
public class Msg {
    private String msgTitle;

    private String sendTime;

    private long msgId;

    private String msgContent;

    private String msgParams;

    private int msgType;

    private String msgImg;

    private long msgBgId;

    private int isRead;

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(String msgParams) {
        this.msgParams = msgParams;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsgImg() {
        return msgImg;
    }

    public void setMsgImg(String msgImg) {
        this.msgImg = msgImg;
    }

    public long getMsgBgId() {
        return msgBgId;
    }

    public void setMsgBgId(long msgBgId) {
        this.msgBgId = msgBgId;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
