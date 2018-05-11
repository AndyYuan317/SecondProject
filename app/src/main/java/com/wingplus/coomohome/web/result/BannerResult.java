package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.WebBanner;

import java.util.List;

/**
 * 轮播图结果
 *
 * @author leaffun.
 *         Create on 2017/10/17.
 */
public class BannerResult extends BaseResult {

    private List<WebBanner> data;

    public List<WebBanner> getData() {
        return data;
    }

    public void setData(List<WebBanner> data) {
        this.data = data;
    }
}
