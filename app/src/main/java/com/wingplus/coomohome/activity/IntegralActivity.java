package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.IntegeralInfo;
import com.wingplus.coomohome.web.entity.ScoreRecord;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.IntegralInfoResult;
import com.wingplus.coomohome.web.result.ScoreRecordResult;

import java.util.List;

/**
 * 积分会员
 *
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public class IntegralActivity extends BaseActivity implements View.OnClickListener {

    private MyRefreshLayout tr;
    private RecyclerView rv;
    private IntegralAdapter adapter;
    private IntegeralInfo info;

    /**
     * 积分记录
     */
    private List<ScoreRecord> srs;

    /**
     * 当前页码
     */
    private int page = Constant.Page.START_PAGE;

    /**
     * 总页数
     */
    private int totalPageCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral);

        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        tr.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                initData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                getNextPageData();
            }
        });
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


    private void initView() {
        tr = findViewById(R.id.tr);
        rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

        adapter = new IntegralAdapter();
        rv.setAdapter(adapter);
    }

    private void initData() {
        page = Constant.Page.START_PAGE;

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SCORE_INFO),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .getParams());
                final IntegralInfoResult result = GsonUtil.fromJson(rs, IntegralInfoResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            info = result.getData();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });

            }
        });

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SCORE_HISTORY),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final ScoreRecordResult result = GsonUtil.fromJson(rs, ScoreRecordResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            srs = result.getData().getResult();
                            page = result.getData().getCurrentPageNo();
                            totalPageCount = result.getData().getTotalPageCount();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(result);
                        }

                    }
                });
            }
        });
    }

    private void getNextPageData() {
        if (page >= totalPageCount) {
            tr.finishLoadmore();
            return;
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SCORE_HISTORY),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page + 1))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final ScoreRecordResult result = GsonUtil.fromJson(rs, ScoreRecordResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishLoadmore();
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            srs.addAll(result.getData().getResult());
                            page = result.getData().getCurrentPageNo();
                            totalPageCount = result.getData().getTotalPageCount();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
            }
        });

    }

    private class IntegralAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new LvVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_integral_lv, parent, false));
            }
            return new RecordVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_wallet_record, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof RecordVH) {
                ((RecordVH) holder).setData(srs.get(position - 1));
            } else if (holder instanceof LvVH) {
                ((LvVH) holder).setData();
            }
        }

        @Override
        public int getItemCount() {
            return 1 + (srs == null ? 0 : srs.size());
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }
    }


    /**
     * 积分签到
     */
    class LvVH extends RecyclerView.ViewHolder {

        private final LinearLayout dailySign;
        private final ImageView img;
        private final TextView user_name;
        private final TextView lv;
        private final ProgressBar lv_bar;
        private final TextView lv_point;

        public LvVH(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            dailySign = itemView.findViewById(R.id.daily_sign);
            img = itemView.findViewById(R.id.img);
            lv = itemView.findViewById(R.id.lv);
            lv_bar = itemView.findViewById(R.id.lv_bar);
            lv_point = itemView.findViewById(R.id.lv_point);

            dailySign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(info == null){
                        ToastUtil.toast("请先登录");
                        return;
                    }

                    if (info.getSigned() != Constant.SignType.ALEADY_SING_TODAY) {
                        APIConfig.getDataIntoView(new Runnable() {
                            @Override
                            public void run() {
                                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SCORE_SIGN),
                                        new ParamsBuilder()
                                                .addParam("token", RuntimeConfig.user.getToken())
                                                .getParams());
                                BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                    initData();
                                }else{
                                    ToastUtil.toastByCode(result);
                                }
                            }
                        });
                    } else {
                        ToastUtil.toast(getApplicationContext(), "今日已经签到了");
                    }
                }
            });


        }

        private void setData() {
            if (RuntimeConfig.userValid()) {
                GlideUtil.GlideInstance(IntegralActivity.this, RuntimeConfig.user.getHeadImgUrl()).into(img);
                user_name.setText(RuntimeConfig.user.getUserName());
            }

            if (info != null) {
                lv.setText(info.getLevel());
                lv_point.setText("积分 " + info.getScore() + "/" + info.getNeedScore());
                lv_bar.setMax(info.getNeedScore());
                lv_bar.setProgress(info.getScore());
            }
        }
    }


    /**
     * 积分记录历史
     */
    class RecordVH extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView count;

        public RecordVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
        }

        public void setData(ScoreRecord sr) {
            name.setText(sr.getDate());
            count.setText(String.valueOf(sr.getScore() >= 0 ? "+" + sr.getScore() :""+sr.getScore()));
            name.setTextColor(UIUtils.getColor(R.color.titleGrey));
            count.setTextColor(UIUtils.getColor(R.color.combinationBlack));
        }
    }

}
