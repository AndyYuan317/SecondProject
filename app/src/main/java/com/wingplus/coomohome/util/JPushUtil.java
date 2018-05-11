package com.wingplus.coomohome.util;

import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.DataConfig;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.StringDataResult;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光PUSH
 *
 * @author leaffun.
 *         Create on 2017/10/30.
 */
public class JPushUtil {

    private static final int max = 3;

    public static void pushRegistrationID(final String token) {
        try {
            tryPush(token, 1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void tryPush(final String token, final int count){
        if(count > max){
            return;
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String registrationID = JPushInterface.getRegistrationID(MallApplication.getContext());
                String s = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_JPUSH_REGISTRATION_ID)
                        , new ParamsBuilder().addParam("token", token)
                                .addParam("platform", "2") //2表示Android
                                .addParam("device", registrationID)
                                .getParams());
                StringDataResult result = GsonUtil.fromJson(s, StringDataResult.class);
                if(result.getResult() != APIConfig.CODE_SUCCESS){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final int num = count + 1;
                    tryPush(token, num);
                }
            }
        });
    }
}
