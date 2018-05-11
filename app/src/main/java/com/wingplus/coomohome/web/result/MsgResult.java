package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.Msg;
import com.wingplus.coomohome.web.entity.PageContent;

/**
 * 消息结果
 *
 * @author leaffun.
 *         Create on 2017/10/28.
 */
public class MsgResult extends BaseResult {
    private PageContent<Msg> data;

    public PageContent<Msg> getData() {
        return data;
    }

    public void setData(PageContent<Msg> data) {
        this.data = data;
    }
}
