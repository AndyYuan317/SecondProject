package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.GoodDetail;

/**
 * 商品详细
 *
 * @author leaffun.
 *         Create on 2017/10/19.
 */
public class GoodDetailResult extends BaseResult {

    private GoodDetail data;

    public GoodDetail getData() {
        return data;
    }

    public void setData(GoodDetail data) {
        this.data = data;
    }
}
