package com.wingplus.coomohome;

import com.wingplus.coomohome.activity.BrandStoryActivity;
import com.wingplus.coomohome.activity.WebActivity;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.web.result.StringDataResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void setOrder(){
        List<String> strings = new ArrayList<>();
        strings.add("粉");
        strings.add("tian");
        strings.add("潜");
        Collections.sort(strings);
        System.out.println(strings.toString());
    }


}