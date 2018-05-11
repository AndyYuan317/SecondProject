package com.wingplus.coomohome.web.entity;

/**
 * 积分信息
 * @author leaffun.
 *         Create on 2017/10/27.
 */
public class IntegeralInfo {

    private long id;

    private String level;

    private int needScore;

    private int score;

    private int signed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getNeedScore() {
        return needScore;
    }

    public void setNeedScore(int needScore) {
        this.needScore = needScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSigned() {
        return signed;
    }

    public void setSigned(int signed) {
        this.signed = signed;
    }
}
