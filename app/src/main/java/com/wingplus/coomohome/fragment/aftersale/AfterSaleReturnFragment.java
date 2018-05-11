package com.wingplus.coomohome.fragment.aftersale;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.TestDataProduceUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.view.LoadingPage;
import com.wingplus.coomohome.web.entity.AfterSaleValid;
import com.wingplus.coomohome.web.entity.Order;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.AfterSaleOrderResult;
import com.wingplus.coomohome.web.result.AfterSaleValidResult;
import com.wingplus.coomohome.web.result.OrderResult;

import java.util.List;

/**
 * 售后-退货
 *
 * @author leaffun.
 *         Create on 2017/10/16.
 */
public class AfterSaleReturnFragment extends AfterSaleBaseFragment {
    private List<AfterSaleValid> asvs;
    private int page = Constant.Page.START_PAGE;
    private int totalPage = 0;
    private MyRefreshLayout tr;
    private OrderAdapter adapter;

    @Override
    public void refreshUIInHere() {
        if (adapter != null) {
            adapter.setData(asvs);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public LoadingPage.LoadResult load() {
        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_VALID_LIST),
                new ParamsBuilder()
                        .addParam("token", RuntimeConfig.user.getToken())
                        .addParam("page", String.valueOf(page))
                        .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                        .getParams());
        final AfterSaleValidResult result = GsonUtil.fromJson(rs, AfterSaleValidResult.class);
        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
            asvs = result.getData().getResult();
            page = result.getData().getCurrentPageNo();
            totalPage = result.getData().getTotalPageCount();
            if (asvs == null || asvs.size() <= 0) {
                return LoadingPage.LoadResult.empty;
            }
            return LoadingPage.LoadResult.success;
        } else {
            return LoadingPage.LoadResult.error;
        }
    }

    @Override
    public View createSuccessLoad() {
        View all = LayoutInflater.from(context).inflate(R.layout.fragment_after_sale, loadingPage, false);
        RecyclerView rv = all.findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new OrderAdapter(asvs, Constant.AfterSaleType.AFTER_SALE_RETURN);
        rv.setAdapter(adapter);

        tr = all.findViewById(R.id.tr);
        tr.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                getNextPageData();
            }
        });
        tr.setEnableRefresh(false);
        tr.setEnableOverScroll(false);
        return all;
    }

    public void getNextPageData() {
        if (page >= totalPage) {
            tr.finishLoadmore();
        } else {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_VALID_LIST),
                            new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("page", String.valueOf(page + 1))
                                    .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                    .getParams());
                    final AfterSaleValidResult result = GsonUtil.fromJson(rs, AfterSaleValidResult.class);
                    if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                        asvs.addAll(result.getData().getResult());
                        page = result.getData().getCurrentPageNo();
                        totalPage = result.getData().getTotalPageCount();
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setData(asvs);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        ToastUtil.toastByCode(result);
                    }
                }
            });
        }
    }
}
