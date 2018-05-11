package com.wingplus.coomohome.web.param;

import java.util.HashMap;

/**
 * 网络请求参数构造
 *
 * @author leaffun.
 *         Create on 2017/10/18.
 */
public class ParamsBuilder {
    private HashMap<String, String> params;

    public ParamsBuilder() {

    }

    public ParamsBuilder addParam(String key, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public HashMap<String, String> getParams() {
        return params;
    }
}
