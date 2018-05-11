package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.GoodShow;

import java.util.List;

/**
 * 猜你喜欢结果
 * @author leaffun.
 *         Create on 2017/11/14.
 */
public class GuessLikeResult extends BaseResult {

    private List<GoodShow> data;

    public List<GoodShow> getData() {
        return data;
    }

    public void setData(List<GoodShow> data) {
        this.data = data;
    }
}
