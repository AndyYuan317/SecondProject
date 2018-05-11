package com.wingplus.coomohome.util;

import com.google.gson.Gson;
import com.wingplus.coomohome.web.result.BaseResult;

/**
 * @author leaffun.
 *         Create on 2017/10/17.
 */
public class GsonUtil {

    private static Gson gson;

    public static <T extends BaseResult> T fromJson(String jsonStr, Class<T> tClass) {
        try {
            if (gson == null) {
                gson = new Gson();
            }
            return gson.fromJson(jsonStr, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Object> T parseJson(String jsonStr, Class<T> tClass) {
        try {
            if (gson == null) {
                gson = new Gson();
            }
            return gson.fromJson(jsonStr, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object object) {
        try {
            if (gson == null) {
                gson = new Gson();
            }
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
