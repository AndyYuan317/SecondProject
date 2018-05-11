package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.PageContent;
import com.wingplus.coomohome.web.entity.ScoreRecord;

/**
 * 积分记录结果
 *
 * @author leaffun.
 *         Create on 2017/10/27.
 */
public class ScoreRecordResult extends BaseResult {

    private PageContent<ScoreRecord> data;

    public PageContent<ScoreRecord> getData() {
        return data;
    }

    public void setData(PageContent<ScoreRecord> data) {
        this.data = data;
    }
}
