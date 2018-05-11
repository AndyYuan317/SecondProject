package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 商品活动
 *
 * @author leaffun.
 *         Create on 2017/11/2.
 */
public class GoodActivity {
    private List<GoodActivityEntity> activity;

    private List<Object> combine;

    public List<GoodActivityEntity> getActivity() {
        return activity;
    }

    public void setActivity(List<GoodActivityEntity> activity) {
        this.activity = activity;
    }

    public List<Object> getCombine() {
        return combine;
    }

    public void setCombine(List<Object> combine) {
        this.combine = combine;
    }
}
