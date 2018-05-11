package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.wingplus.coomohome.web.entity.Welfare;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.WelfareResult;

import java.util.List;

/**
 * 公益活动
 *
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class WelfareActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rv;
    private List<Welfare> ws;
    private WelfareAdapter adapter;

    private int page = Constant.Page.START_PAGE;
    private int totalPageCount = 0;
    private MyRefreshLayout tr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare);

        initView();
        initEvent();
        tr.startRefresh();
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
        rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new WelfareAdapter();
        rv.setAdapter(adapter);

        tr = findViewById(R.id.tr);
    }

    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SOCIAL_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(Constant.Page.START_PAGE))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final WelfareResult welfareResult = GsonUtil.fromJson(rs, WelfareResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (welfareResult != null && welfareResult.getResult() == APIConfig.CODE_SUCCESS) {
                            ws = welfareResult.getData().getResult();
                            page = welfareResult.getData().getCurrentPageNo();
                            totalPageCount = welfareResult.getData().getTotalPageCount();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(welfareResult);
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
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SOCIAL_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final WelfareResult welfareResult = GsonUtil.fromJson(rs, WelfareResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishLoadmore();
                        if (welfareResult != null && welfareResult.getResult() == APIConfig.CODE_SUCCESS) {
                            ws.addAll(welfareResult.getData().getResult());
                            page = welfareResult.getData().getCurrentPageNo();
                            totalPageCount = welfareResult.getData().getTotalPageCount();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(welfareResult);
                        }

                    }
                });
            }
        });
    }

    private class WelfareAdapter extends RecyclerView.Adapter<WelfareVH> {

        @Override
        public WelfareVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WelfareVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_news, parent, false));
        }

        @Override
        public void onBindViewHolder(WelfareVH holder, int position) {
            holder.setData(ws.get(position));
        }

        @Override
        public int getItemCount() {
            return ws == null ? 0 : ws.size();
        }
    }

    class WelfareVH extends RecyclerView.ViewHolder {

        private final TextView time;
        private final TextView title;
        private final TextView des;
        private final TextView horizontal;
        private final ImageView img;
        private final CardView cv;

        WelfareVH(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            des = itemView.findViewById(R.id.des);
            horizontal = itemView.findViewById(R.id.horizontal);
            img = itemView.findViewById(R.id.img);
            cv = itemView.findViewById(R.id.cv);
        }

        public void setData(final Welfare n) {
            des.setVisibility(View.GONE);
            horizontal.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            GlideUtil.GlideWithPlaceHolder(getApplicationContext(), n.getActivity_img()).into(img);


            time.setVisibility(View.GONE);
            title.setText(n.getActivity_name());
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegularActivity.class);
                    intent.putExtra(Constant.Key.KEY_REGULAR_TYPE, Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_WELFARE);
                    intent.putExtra(Constant.Key.KEY_WELFARE_NAME, n.getActivity_name());
                    intent.putExtra(Constant.Key.KEY_WELFARE_ID, n.getActivity_id());
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        }
    }
}
