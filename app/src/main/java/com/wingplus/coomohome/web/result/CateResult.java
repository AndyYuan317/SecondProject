package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.Category;

import java.util.List;

/**
 * 分类结果
 * @author leaffun.
 *         Create on 2017/10/27.
 */
public class CateResult extends BaseResult {

    private List<Category> data;

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }
}
