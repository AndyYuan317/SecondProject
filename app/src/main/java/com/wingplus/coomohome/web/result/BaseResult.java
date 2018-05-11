package com.wingplus.coomohome.web.result;

/**
 * API结果基类
 *
 * @author leaffun.
 *         Create on 2017/10/17.
 */
public class BaseResult {

    private int result;

    private String message;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
