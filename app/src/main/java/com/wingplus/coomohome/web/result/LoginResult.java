package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.User;

/**
 * 登录结果
 *
 * @author leaffun.
 *         Create on 2017/10/18.
 */
public class LoginResult extends BaseResult {

    private User data;


    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
