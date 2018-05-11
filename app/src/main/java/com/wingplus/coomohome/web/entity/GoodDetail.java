package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 商品详细
 * @author leaffun.
 *         Create on 2017/10/19.
 */
public class GoodDetail extends GoodShow {

    /**
     * 规格：属性集
     */
    private List<GoodSpec> specList;

    /**
     * 是否收藏
     */
    private int favorite;

    public List<GoodSpec> getSpecList() {
        return specList;
    }

    public void setSpecList(List<GoodSpec> specList) {
        this.specList = specList;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
