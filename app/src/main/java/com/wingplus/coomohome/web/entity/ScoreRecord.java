package com.wingplus.coomohome.web.entity;

/**
 * 积分记录
 *
 * @author leaffun.
 *         Create on 2017/10/27.
 */
public class ScoreRecord {

    private long id;

    private String date;

    private int score;

    private String reason;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
