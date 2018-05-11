package com.wingplus.coomohome.fragment.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.web.entity.Order;
import com.wingplus.coomohome.view.LoadingPage;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.OrderResult;

import java.util.List;

/**
 * 全部订单
 *
 * @author leaffun.
 *         Create on 2017/10/9.
 */
public class OrderAllFragment extends OrderBaseFragment {


    private List<Order> orders;
    private OrderAdapter adapter;
    private RecyclerView rv;
    private LinearLayoutManager manager;

    @Override
    protected void finishRefresh() {
        if (rv != null && tr != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
            int position = layoutManager.findFirstVisibleItemPosition();
            if (position == 0) {
                View firstView = layoutManager.findViewByPosition(position);
                int dis = firstView.getTop();
                int paddingTop = rv.getPaddingTop();
                boolean onTop = dis >= paddingTop - 20;
                tr.setEnableRefresh(onTop);
            } else {
                tr.setEnableRefresh(false);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View createSuccessLoad() {
        View all = LayoutInflater.from(context).inflate(R.layout.fragment_order_all, loadingPage, false);
        rv = all.findViewById(R.id.rv);
        manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new OrderAdapter(orders);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                if (position == 0) {
                    View firstView = layoutManager.findViewByPosition(position);
                    int dis = firstView.getTop();
                    int paddingTop = recyclerView.getPaddingTop();
                    boolean onTop = dis >= paddingTop - 20;
                    tr.setEnableRefresh(onTop);
                } else {
                    tr.setEnableRefresh(false);
                }
            }
        });
        return all;
    }

    @Override
    public void refreshUIInHere() {
        if (adapter != null) {
            adapter.setData(orders);
            adapter.notifyDataSetChanged();
        }
        if (tr != null) {
            tr.finishRefreshing();
        }
    }

    @Override
    public LoadingPage.LoadResult load() {
        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_LIST)
                , new ParamsBuilder()
                        .addParam("token", RuntimeConfig.user.getToken())
                        .addParam("state", "" + Constant.OrderStatus.ORDER_STATUS_INT_ALL)
                        .getParams());

        final OrderResult result = GsonUtil.fromJson(rs, OrderResult.class);
        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
            if (result.getData() != null && result.getData().size() > 0) {
                orders = result.getData();
                return LoadingPage.LoadResult.success;
            } else {
                return LoadingPage.LoadResult.empty;
            }

        } else {
            ToastUtil.toastByCode(result);
            return LoadingPage.LoadResult.empty;
        }
    }

    @Override
    public void sureGet() {
        loadData();
    }

    @Override
    protected boolean needCommit() {
        return true;
    }
}
