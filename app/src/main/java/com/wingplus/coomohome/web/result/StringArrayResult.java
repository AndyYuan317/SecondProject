package com.wingplus.coomohome.web.result;

import java.util.List;

/**
 * 字符串数组结果
 * @author leaffun.
 *         Create on 2017/11/19.
 */
public class StringArrayResult extends BaseResult {
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
