package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.RechargeRecord;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.RechargeRecordResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import java.util.List;

/**
 * 我的钱包页
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class MineWalletActivity extends BaseActivity implements View.OnClickListener {

    private MyRefreshLayout tr;
    private RecyclerView rv;
    private List<RechargeRecord> rrs;
    private double money;
    private int page;
    private int totalPageCount;
    private WalletAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_wallet);
        initView();
        initEvent();
        tr.startRefresh();
    }

    private void initView() {
        tr = findViewById(R.id.tr);
        rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new WalletAdapter();
        rv.setAdapter(adapter);
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

    private void initData() {
        page = 0;
        totalPageCount = 0;
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_WALLET_INFO)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .getParams());
                final StringDataResult moneyResult = GsonUtil.fromJson(rs, StringDataResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (moneyResult != null && moneyResult.getResult() == APIConfig.CODE_SUCCESS) {
                            money = Double.parseDouble(moneyResult.getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(moneyResult);
                        }
                    }
                });
            }
        });

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_WALLET_HISTORY)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final RechargeRecordResult record = GsonUtil.fromJson(rs, RechargeRecordResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (record != null && record.getResult() == APIConfig.CODE_SUCCESS) {
                            rrs = record.getData().getResult();
                            page = record.getData().getCurrentPageNo();
                            totalPageCount = record.getData().getTotalPageCount();
                            adapter.notifyDataSetChanged();
                            tr.finishRefreshing();
                        } else {
                            ToastUtil.toastByCode(record);
                        }
                    }
                });
            }
        });
    }

    private void getNextPageData() {
        if (page >= totalPageCount) {
            //todo 没有更多数据了
            tr.finishLoadmore();
            return;
        }
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_WALLET_HISTORY)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page + 1))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final RechargeRecordResult record = GsonUtil.fromJson(rs, RechargeRecordResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishLoadmore();
                        if (record != null && record.getResult() == APIConfig.CODE_SUCCESS) {
                            rrs.addAll(record.getData().getResult());
                            page = record.getData().getCurrentPageNo();
                            totalPageCount = record.getData().getTotalPageCount();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(record);
                        }
                    }
                });

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


    class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new WalletVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_wallet_wallet, parent, false));
            }
            return new RecordVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_wallet_record, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof WalletVH) {
                ((WalletVH) holder).setData(money);
            } else if (holder instanceof RecordVH) {
                ((RecordVH) holder).setData(rrs.get(position - 1));
            }

        }

        @Override
        public int getItemCount() {
            return (rrs == null ? 0 : rrs.size()) + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position > 0 ? 1 : 0;
        }
    }

    class WalletVH extends RecyclerView.ViewHolder {

        private final TextView money;
        private final TextView toRecharge;

        public WalletVH(View itemView) {
            super(itemView);
            money = itemView.findViewById(R.id.money);
            toRecharge = itemView.findViewById(R.id.to_recharge);
            toRecharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtil.toast(getApplicationContext(), "去充值");
                }
            });
        }

        public void setData(double rmb) {
            money.setText(String.valueOf(rmb));
        }
    }

    class RecordVH extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView count;

        public RecordVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
        }

        public void setData(RechargeRecord rr) {
            name.setText(rr.getDate());
            count.setText(rr.getAmount());
        }
    }


}
