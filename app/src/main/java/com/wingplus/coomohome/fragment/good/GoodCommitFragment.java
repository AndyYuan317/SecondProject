package com.wingplus.coomohome.fragment.good;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.adapter.GridAdapter;
import com.wingplus.coomohome.component.GridViewMeasure;
import com.wingplus.coomohome.component.MyRatingStar;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodCommit;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.GoodCommitResult;

import java.util.List;


/**
 * 商品详情-评价
 *
 * @author leaffun.
 *         Create on 2017/9/14.
 */
public class GoodCommitFragment extends Fragment {

    private View rootView;
    private RecyclerView recycler;
    private Context context;
    private MyRefreshLayout tr;

    private long gId;
    private List<GoodCommit> commits;

    //分页
    private int totalPage = 0;
    private int page = Constant.Page.START_PAGE;
    private CommitAdapter adapter;
    private TextView empty;

    public static GoodCommitFragment newInstance() {

        return new GoodCommitFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_good_commit, container, false);
        context = getContext();
        initView();
        initData();
        return rootView;
    }

    private void initView() {
        tr = rootView.findViewById(R.id.tr);
        recycler = rootView.findViewById(R.id.commits);
        empty = rootView.findViewById(R.id.empty);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);
        adapter = new CommitAdapter();
        recycler.setAdapter(adapter);

        tr.setEnableLoadmore(true);
        tr.setEnableRefresh(false);
        tr.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                getNextPageData();
            }
        });
    }

    private void initData() {
        ((GoodsDetailActivity) getActivity()).setInitListener(new GoodsDetailActivity.InitGoodListener() {
            @Override
            public void initGood(final long goodId) {
                gId = goodId;
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_EVALUATE_LIST)
                                , new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("goodsId", "" + gId)
                                        .addParam("page", "" + page)
                                        .addParam("pageSize", "" + Constant.Page.COMMON_PAGE_SIZE)
                                        .getParams());
                        final GoodCommitResult result = GsonUtil.fromJson(rs, GoodCommitResult.class);
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalPage = result.getData().getTotalPageCount();
                                    page = result.getData().getCurrentPageNo();
                                    commits = result.getData().getResult();
                                    adapter.notifyDataSetChanged();
                                    if(commits.size()<=0){
                                        recycler.setVisibility(View.GONE);
                                        empty.setVisibility(View.VISIBLE);
                                    }else{
                                        recycler.setVisibility(View.VISIBLE);
                                        empty.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });

            }
        });
    }

    private void getNextPageData() {
        if (page >= totalPage) {
            tr.finishLoadmore();
            return;
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_EVALUATE_LIST)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("goodsId", "" + gId)
                                .addParam("page", String.valueOf(page+1))
                                .addParam("pageSize", "" + Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final GoodCommitResult result = GsonUtil.fromJson(rs, GoodCommitResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            tr.finishLoadmore();
                            totalPage = result.getData().getTotalPageCount();
                            page = result.getData().getCurrentPageNo();
                            commits = result.getData().getResult();
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });
    }


    private class CommitAdapter extends RecyclerView.Adapter<CommitViewHolder> {

        @Override
        public CommitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommitViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_commit, parent, false));
        }

        @Override
        public void onBindViewHolder(CommitViewHolder holder, int position) {
            GoodCommit goodCommit = commits.get(position);
            GlideUtil.GlideWithPlaceHolder(context, goodCommit.getHeadImg()).into(holder.userHeadImg);
            holder.userName.setText(goodCommit.getName());
            holder.commitStar.setRating(goodCommit.getScore());
            holder.commitTime.setText(goodCommit.getDate());
            holder.userLv.setText(goodCommit.getLevel());

            if (goodCommit.getComment().length() == 0) {
                holder.commitTxt.setVisibility(View.GONE);
            } else {
                holder.commitTxt.setVisibility(View.VISIBLE);
                holder.commitTxt.setText(goodCommit.getComment());
            }

            if (!(goodCommit.getImgUrl() != null && goodCommit.getImgUrl().size() == 1 && goodCommit.getImgUrl().get(0).length() == 0)) {
                holder.commitImg.setVisibility(View.VISIBLE);
                holder.commitImg.setAdapter(new GridAdapter(goodCommit.getImgUrl(), getActivity()));
            } else {
                holder.commitImg.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return commits == null ? 0 : commits.size();
        }
    }

    class CommitViewHolder extends RecyclerView.ViewHolder {
        private ImageView userHeadImg;
        private TextView userName;
        private MyRatingStar commitStar;
        private TextView commitTime;
        private TextView commitTxt;
        private TextView userLv;
        private GridViewMeasure commitImg;

        CommitViewHolder(View itemView) {
            super(itemView);
            userHeadImg = itemView.findViewById(R.id.user_headImg);
            userName = itemView.findViewById(R.id.user_name);
            commitStar = itemView.findViewById(R.id.commit_star);
            commitTime = itemView.findViewById(R.id.commit_time);
            commitTxt = itemView.findViewById(R.id.commit_txt);
            commitImg = itemView.findViewById(R.id.commit_img);
            userLv = itemView.findViewById(R.id.user_lv);
        }
    }


}
