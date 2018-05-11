package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.MyFlowLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.NetUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.SearchWordsResult;

import java.util.List;

/**
 * 搜索页
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private EditText search_edit;
    private MyFlowLayout hot_search;
    private MyFlowLayout history_search;
    private ImageView history_delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {

        search_edit = findViewById(R.id.search_edit);
        hot_search = findViewById(R.id.hot_search);
        history_search = findViewById(R.id.history_search);
        history_delete = findViewById(R.id.history_delete);
    }

    private void initEvent() {
        //配合布局在软键盘enter位置显示【搜索】
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    doSearch(textView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        history_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SEARCH_CLEAR_HISTORY),
                                new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .getParams());
                        SearchWordsResult searchWordsResult = GsonUtil.fromJson(rs, SearchWordsResult.class);
                        if (searchWordsResult != null && searchWordsResult.getResult() == APIConfig.CODE_SUCCESS) {
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    notyData(history_search, null);
                                }
                            });
                        } else {
                            ToastUtil.toastByCode(searchWordsResult);
                        }
                    }
                });
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(search_edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initData() {

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SEARCH_HOT_WORDS),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .getParams());
                final SearchWordsResult searchWordsResult = GsonUtil.fromJson(rs, SearchWordsResult.class);
                if (searchWordsResult != null && searchWordsResult.getResult() == APIConfig.CODE_SUCCESS) {
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            notyData(hot_search, searchWordsResult.getData());
                        }
                    });

                } else {
                    ToastUtil.toastByCode(searchWordsResult);
                }
            }
        });

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SEARCH_HISTORY_WORDS),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .getParams());
                final SearchWordsResult searchWordsResult = GsonUtil.fromJson(rs, SearchWordsResult.class);
                if (searchWordsResult != null && searchWordsResult.getResult() == APIConfig.CODE_SUCCESS) {
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            notyData(history_search, searchWordsResult.getData());
                        }
                    });
                } else {
//                    ToastUtil.toastByCode(searchWordsResult);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(search_edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            finish();
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            default:
                break;
        }
    }

    /**
     * 刷新数据
     *
     * @param parent
     * @param childs 传递null时，清空数据
     */
    private void notyData(MyFlowLayout parent, List<String> childs) {
        parent.removeAllViews();
        if (childs == null || childs.isEmpty()) {
            parent.setVisibility(View.GONE);
            return;
        }else{
            parent.setVisibility(View.VISIBLE);
        }
        for (String s : childs) {
            final TextView hisText = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_search_hot_text, parent, false);
            hisText.setText(s);
            hisText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doSearch(hisText.getText().toString());
                }
            });
            parent.addView(hisText);
        }
    }


    /**
     * 开始搜索（保存、刷新、跳转）
     *
     * @param s
     */
    private void doSearch(String s) {
        if (s == null || s.trim().equals("")) {
            ToastUtil.toast(getApplicationContext(), "请输入搜索关键词");
            return;
        }


        if (NetUtil.IsActivityNetWork(getApplicationContext())) {
            Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
            RuntimeConfig.searchWord = s;
            startActivity(intent);
            finish();//如果直接结束掉，就不必刷新数据了。
        } else {
            ToastUtil.toast(getApplicationContext(), getString(R.string.word_please_check_network));
        }


        if ("SearchResultActivity".equals(getIntent().getStringExtra("from"))) {
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
        } else {
            overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
        }
    }
}