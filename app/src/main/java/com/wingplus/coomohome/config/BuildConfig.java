package com.wingplus.coomohome.config;

/**
 * @author leaffun.
 *         Create on 2017/10/17.
 */
public class BuildConfig {

    public static class Weixin{

        public static final String state = "coomo_wx_login_123";
        public static final String state_bind = "coomo_wx_bind_123";

        public static final String APP_ID = "wx2a872d1bb6094130";
        private static final String APP_SECRET = "fb6af634bd5a8c6dd167b3000f2e3b4b";

        public static final String ACCESS_TOKEN_URL_GET = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                APP_ID +
                "&secret=" +
                APP_SECRET +
                "&code=" +
                "CODE" +
                "&grant_type=authorization_code";

        public static final String ACCESS_TOKEN_VALID_URL_GET = "https://api.weixin.qq.com/sns/auth?access_token=" +
                "ACCESS_TOKEN" +
                "&openid=" +
                "OPENID";


        public static final String REFRESH_ACCESS_TOKEN_URL_GET = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" +
                APP_ID +
                "&grant_type=refresh_token&" +
                "refresh_token=" +
                "REFRESH_TOKEN";

        public static final String USER_INFO_URL_GET = "https://api.weixin.qq.com/sns/userinfo?access_token=" +
                "ACCESS_TOKEN" +
                "&openid=" +
                "OPENID";

        public static String getAccessTokenUrl(String APP_ID, String APP_SECRET, String CODE){
            return ACCESS_TOKEN_URL_GET.replace("APPID", APP_ID)
                    .replace("APPSECRET", APP_SECRET)
                    .replace("CODE", CODE);
        }
    }

    public static class JMessage{


    }



}
