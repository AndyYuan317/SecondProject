package com.wingplus.coomohome.util;

import android.util.Log;

import com.google.gson.Gson;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.MainActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Helper class of Http request and response.
 * Created by wangyn on 15/7/12.
 */
public class HttpUtil {
    private final static String TAG = "HttpUtil";

    public final static int GET = 0;
    public final static int POST = 1;

    private static final OkHttpClient client;
    private static final MediaType JSON;
    private static final MediaType FORM_DATA;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(Constant.Http.CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(Constant.Http.READ_TIME_OUT, TimeUnit.SECONDS);
        client = builder.build();

        JSON = MediaType.parse("application/json; charset=utf-8");
        FORM_DATA = MediaType.parse("form-data; charset=utf-8");
    }

    public static String GetDataFromNetByPost(String strUrl, HashMap<String, String> params) {
        if (!NetUtil.IsActivityNetWork(MallApplication.getContext())) {
            return "";
        }

        try {
            return PostDataFromNetByForm(strUrl, params);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
            e.printStackTrace();
        }
        return "";
    }

    public static String GetDateFromNetByGet(String strUrl, HashMap<String, String> params) {
        if (!NetUtil.IsActivityNetWork(MallApplication.getContext())) {
            return "";
        }

        try {
            return GetDataFromNet(strUrl, params, GET);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
            e.printStackTrace();
        }
        return "";
    }

    /**
     * url请求
     *
     * @param strUrl
     * @param params
     * @param type
     * @return
     */
    public static String GetDataFromNet(String strUrl, HashMap<String, String> params, int type) {
        try {
            if (type == GET) {
                return GetDataFromNet(strUrl, params);
            } else if (type == POST) {
                return PostDataFromNet(strUrl, params);
            } else {
                return GetDataFromNet(strUrl, params);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return "";
    }

    /**
     * json为参数的post提交
     *
     * @param strUrl
     * @param json
     * @return
     * @throws IOException
     */
    public static String PostJson(String strUrl, String json) throws IOException {
        String result = "";

        RequestBody formBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(strUrl).post(formBody).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public static String PostFile(String strUrl, File file) throws IOException {
        String result = "";

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(file.getPath())), file);
        builder.addFormDataPart("file", file.getName(), fileBody);
        builder.addFormDataPart("fileName", file.getName());

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(strUrl).post(requestBody).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Post方式请求
     *
     * @param strUrl
     * @param params
     * @return
     * @throws Exception
     */
    private static String PostDataFromNet(String strUrl, HashMap<String, String> params) throws Exception {
        String result = "";

        RequestBody formBody = RequestBody.create(JSON, new Gson().toJson(params));
        Request request = new Request.Builder().url(strUrl).post(formBody).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Post方式form-data
     */
    private static String PostDataFromNetByForm(String strUrl, HashMap<String, String> params) {
        long startTime = System.currentTimeMillis();

        String paramStr = "";
        String result = "";
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    entry.setValue("");
                }
                builder.add(entry.getKey(), entry.getValue());
                paramStr += ("(" + entry.getKey() + "->" + entry.getValue() + ")");
            }
        }
        FormBody formBody = builder.build();

        Request request = new Request.Builder().url(strUrl).post(formBody).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        //破除4000个字符的限制
        String format = "[" + strUrl + "][" + paramStr + "]" + result;
        int max = 3000;
        int length = format.length();
        if (length > max) {
            int n = length / max + 1;
            for (int i = 0; i < n; i++) {
                if (i == n - 1) {
                    LogUtil.i(TAG, format.substring(i * max));
                } else {
                    LogUtil.i(TAG, format.substring(i * max, (i + 1) * max));
                }
            }
        } else {
            LogUtil.i(TAG, format);
        }

        long endTime = System.currentTimeMillis();

        try {
            long bet = endTime - startTime;
            if (bet < 400) {
                Thread.sleep(400 - bet);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get方式请求
     *
     * @param strUrl
     * @param params
     * @return
     * @throws Exception
     */
    private static String GetDataFromNet(String strUrl, HashMap<String, String> params) throws Exception {
        String result = "";
        String url = generateGetUrl(strUrl, params);

        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 拼接生成url
     *
     * @param strUrl
     * @param params
     * @return
     */
    private static String generateGetUrl(String strUrl, HashMap<String, String> params) {
        if (!strUrl.contains("?")) {
            strUrl += "?";
        }

        if (params == null) {
            return strUrl;
        }

        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            strUrl += (entry.getKey() + "=" + entry.getValue());
        }

        return strUrl;
    }

    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

}
