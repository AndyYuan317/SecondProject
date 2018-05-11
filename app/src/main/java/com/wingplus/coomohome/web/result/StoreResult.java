package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.Store;

import java.util.List;

/**
 * 是的
 * @author leaffun.
 *         Create on 2017/10/25.
 */
public class StoreResult extends BaseResult {

    private List<Store> data;

    public List<Store> getData() {
        return data;
    }

    public void setData(List<Store> data) {
        this.data = data;
    }
}
