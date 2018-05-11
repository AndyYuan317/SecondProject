package com.wingplus.coomohome.web.result;

import java.util.List;

/**
 * 搜索热词
 * @author leaffun.
 *         Create on 2017/10/19.
 */
public class SearchWordsResult extends BaseResult {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }


}
