package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.StringDataResult;

/**
 * 浏览器视图
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class WebActivity extends BaseActivity implements View.OnClickListener {


    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback;
    private LinearLayout mLinearLayout;
    private TextView title;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        initWeb();
        test();

    }

    private void test() {
        url = getIntent().getStringExtra(Constant.Key.KEY_WEB_URL);
        LogUtil.i("web view url: ", url == null ? "null" : url);
        if (url != null && !url.isEmpty()) {
            try {
                if (url.startsWith(getResources().getString(R.string.app_url_scheme)) || url.startsWith("alipays://") ||
                        url.startsWith("mailto://") || url.startsWith("tel://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    finish();
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            url = getIntent().getDataString();
        }

        //拦截自家商品链接
        String goodStr = "";
        if (url == null || url.isEmpty()) return;
        String myGoods = "goods-";
        String myEnd = ".html";
        if (url.contains(myGoods)) {
            int begin = url.lastIndexOf(myGoods) + myGoods.length();
            int end = url.indexOf(myEnd);
            goodStr = url.substring(begin, end);
        }

        long goodsId = 0;
        long agentId = 0;
        try {
            goodsId = Long.parseLong(goodStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (goodsId != 0) {
            //1.检测到产品详情页
            Intent intent = new Intent(WebActivity.this, GoodsDetailActivity.class);
            intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(goodsId));
            startActivity(intent);
            finish();
            return;
        } else if (RuntimeConfig.userValid()) {
            //2.登录状态下检测到推荐注册网址，绑定网址中的推荐人
            String myRegister = "register.html?agentid=";
            int begin = url.lastIndexOf(myRegister) + myRegister.length();
            final String agentStr = url.substring(begin);
            try {
                agentId = Long.parseLong(agentStr);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if (agentId != 0) {
                //通过接口绑定推荐人即可结束本页面。
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String result = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AGENT_BIND)
                                , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("agentId", agentStr)
                                        .getParams());
                        StringDataResult stringDataResult = GsonUtil.fromJson(result, StringDataResult.class);
                        ToastUtil.toast(stringDataResult.getMessage());
                        finish();
                    }
                });
                return;
            }
        }

        //3.品牌故事页面
        long brandGoodsId = 0;
        String myBrandStory = "brand_story.html?goodsId=";
        int begin = url.lastIndexOf(myBrandStory) + myBrandStory.length();
        final String brandGoodsStr = url.substring(begin);
        try {
            brandGoodsId = Long.parseLong(brandGoodsStr);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if(brandGoodsId != 0){
            //跳转一个品牌故事页面
            Intent intent = new Intent(WebActivity.this, BrandStoryActivity.class);
            intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(brandGoodsId));
            startActivity(intent);
            finish();
        }


        AgentWeb.with(WebActivity.this)//传入Activity
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go(url);

    }


    private void initView() {
        mLinearLayout = findViewById(R.id.web_content);
        title = findViewById(R.id.web_title);
    }

    private void initWeb() {
        mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
            @Override
            public void onReceivedTitle(WebView view, String webTitle) {
                LogUtil.i("webView_title: ", webTitle + "---" + view.getUrl() + "------" + view.getOriginalUrl());
                title.setText(webTitle);
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                finish();
                break;
            default:
                break;
        }
    }
}
