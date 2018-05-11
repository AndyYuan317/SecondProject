package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.WebBanner;

/**
 * 首页广告
 *
 * @author leaffun.
 *         Create on 2017/10/17.
 */
public class AdvResult extends BaseResult {

    private WebBanner data;

    public WebBanner getData() {
        return data;
    }

    public void setData(WebBanner data) {
        this.data = data;
    }
}
