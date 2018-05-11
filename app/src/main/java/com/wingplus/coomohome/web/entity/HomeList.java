package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 首页信息
 * @author leaffun.
 *         Create on 2017/10/21.
 */
public class HomeList {
    private List<WebBanner> banner;

    private Video video;

    private List<HomeFloor> floor;

    public List<WebBanner> getBanner() {
        return banner;
    }

    public void setBanner(List<WebBanner> banner) {
        this.banner = banner;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public List<HomeFloor> getFloor() {
        return floor;
    }

    public void setFloor(List<HomeFloor> floor) {
        this.floor = floor;
    }
}
