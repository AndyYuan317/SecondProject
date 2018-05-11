package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.just.library.AgentWebView;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.StringDataResult;

/**
 * 活动规则页
 *
 * @author leaffun.
 *         Create on 2017/9/5.
 */
public class RegularActivity extends BaseActivity implements View.OnClickListener {

    private TextView txt;
    private ImageView img;
    private TextView title;
    private AgentWebView web;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular);

        initView();
        initData();
    }

    private void initView() {
        txt = findViewById(R.id.txt_content);
        img = findViewById(R.id.img);
        title = findViewById(R.id.title);
        web = findViewById(R.id.web);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setDefaultTextEncodingName("utf-8");
        web.getSettings().setJavaScriptEnabled(true);
    }

    private void initData() {
        String type = getIntent().getStringExtra(Constant.Key.KEY_REGULAR_TYPE);
        if (type == null) {
            finish();
            return;
        }
        switch (type) {
            case Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_ACTIVITY_REGULAR:
                //富文本
                web.setVisibility(View.VISIBLE);
                txt.setVisibility(View.GONE);


                final long activityId = getIntent().getLongExtra(Constant.Key.KEY_ACTIVITY_ID, 0);
                title.setText("活动规则");

                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ACTIVITY_RULE),
                                new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("id", String.valueOf(activityId))
                                        .getParams());
                        final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                    web.loadDataWithBaseURL(null, result.getData(), "text/html", "UTF-8", null);
                                } else {
                                    ToastUtil.toastByCode(result);
                                }
                            }
                        });
                    }
                });
                break;
            case Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_ANNOUNCEMENT:
                //文本+图
                web.setVisibility(View.GONE);
                txt.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);

                String msgTitle = getIntent().getStringExtra(Constant.Key.KEY_MSG_TITLE);
                String msgImg = getIntent().getStringExtra(Constant.Key.KEY_MSG_IMG);
                String msgContent = getIntent().getStringExtra(Constant.Key.KEY_MSG_CONTENT);
                title.setText(msgTitle);
                GlideUtil.GlideWithPlaceHolder(RegularActivity.this, msgImg).into(img);
                if(msgImg == null ||"".equals(msgImg)){
                    img.setVisibility(View.GONE);
                }
                txt.setText(msgContent);
                break;
            case Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_WELFARE:
                //富文本
                web.setVisibility(View.VISIBLE);
                txt.setVisibility(View.GONE);

                final long welfareId = getIntent().getLongExtra(Constant.Key.KEY_WELFARE_ID, 0);
                String welfareName = getIntent().getStringExtra(Constant.Key.KEY_WELFARE_NAME);
                title.setText(welfareName);
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SOCIAL_INFO)
                                , new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("id", String.valueOf(welfareId))
                                        .getParams());
                        final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                    String data = result.getData();
                                    web.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
                                } else {
                                    ToastUtil.toastByCode(result);
                                }
                            }
                        });
                    }
                });
                break;
            case Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_ABOUT_US:
                //富文本
                web.setVisibility(View.VISIBLE);
                txt.setVisibility(View.GONE);

                title.setText("关于我们");
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_HELP_INFO)
                                , new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("title", "关于我们")
                                        .getParams());
                        final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                    String data = result.getData();
                                    web.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
                                } else {
                                    ToastUtil.toastByCode(result);
                                }
                            }
                        });
                    }
                });
                break;
            case Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_PROTOCOL:
                //富文本
                web.setVisibility(View.VISIBLE);
                txt.setVisibility(View.GONE);

                title.setText("服务协议");
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_HELP_INFO)
                                , new ParamsBuilder()
                                        .addParam("title", "注册服务条款")
                                        .getParams());
                        final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                    String data = result.getData();
                                    web.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
                                } else {
                                    ToastUtil.toastByCode(result);
                                }
                            }
                        });
                    }
                });
                break;
            case Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_INVITED:
                //富文本
                web.setVisibility(View.VISIBLE);
                txt.setVisibility(View.GONE);

                title.setText("邀请规则");
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_HELP_INFO)
                                , new ParamsBuilder()
                                        .addParam("title", "邀请规则")
                                        .getParams());
                        final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                    String data = result.getData();
                                    web.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
                                } else {
                                    ToastUtil.toastByCode(result);
                                }
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
