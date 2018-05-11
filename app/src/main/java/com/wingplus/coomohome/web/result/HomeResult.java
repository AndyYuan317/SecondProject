package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.HomeList;

/**
 * 首页信息
 * @author leaffun.
 *         Create on 2017/10/21.
 */
public class HomeResult extends BaseResult {

    private HomeList data;

    public HomeList getData() {
        return data;
    }

    public void setData(HomeList data) {
        this.data = data;
    }
}
