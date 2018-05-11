package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.just.library.AgentWebView;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.manager.ThreadManager;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.StringDataResult;

import static com.wingplus.coomohome.config.Constant.Key.KEY_GOOD_ID_OR_SN;

/**
 * 品牌故事页面
 * Created by leaffun on 2018/3/13.
 */

public class BrandStoryActivity extends BaseActivity implements View.OnClickListener{

    private AgentWebView webView;
    private String html = "没有找到品牌故事";
    private String goodsId = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_story);
        initView();
        initData();
    }

    private void initData() {
        goodsId = getIntent().getStringExtra(Constant.Key.KEY_GOOD_ID_OR_SN);
        if(goodsId != null && goodsId.length()>0){
            getBrandStoryHtml(goodsId);
        }
    }

    private void initView() {
        webView = findViewById(R.id.img);
    }

    public void setData() {
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    private void getBrandStoryHtml(final String goodsId) {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String resultStr = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrlForBrand(APIConfig.API_GOODS_BRAND)+"?goodsId="+goodsId
                        , null);
                StringDataResult result = GsonUtil.fromJson(resultStr, StringDataResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS && result.getData() != null && result.getData().length() > 0) {
                    html = result.getData();
                }
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                finish();
                break;
            case R.id.good_detail:
                if(goodsId.length()>0) {
                    Intent intent = new Intent(BrandStoryActivity.this, GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, goodsId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
                break;
            default:
                break;
        }
    }
}
