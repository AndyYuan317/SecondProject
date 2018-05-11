package com.wingplus.coomohome.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.CategoryActivity;
import com.wingplus.coomohome.activity.SearchActivity;
import com.wingplus.coomohome.component.ResizableImageView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.BaseFragment;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.OrderUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.Category;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.CateResult;

import java.util.List;

/**
 * 分类
 *
 * @author leaffun.
 *         Create on 2017/8/26.
 */
public class CategoryFragment extends BaseFragment {

    private LayoutInflater myInflater;
    private View rootView;
    private List<Category> mDatas;
    private RecyclerView recycler;
    private Context context;
    private ImageView search;
    private CateAdapter adapter;


    public CategoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflater = inflater;
        context = getContext();
        if (rootView == null) {
            rootView = myInflater.inflate(R.layout.fragment_main_category, container, false);
        }
        initView();
        initEvent();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public void initView() {
        recycler = rootView.findViewById(R.id.cate_rv);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(manager);
        adapter = new CateAdapter();
        recycler.setAdapter(adapter);

        search = rootView.findViewById(R.id.category_search);
    }

    private void initEvent() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
            }
        });
    }

    public void initData() {
        if (RuntimeConfig.mainCate != null && RuntimeConfig.mainCate.size() > 0) {
            mDatas = RuntimeConfig.mainCate;
            adapter.notifyDataSetChanged();
            return;
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_TOP_CATEGORY)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("parent", "0")//0表示获取一级分类
                                .getParams());
                final CateResult cateResult = GsonUtil.fromJson(rs, CateResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cateResult != null && cateResult.getResult() == APIConfig.CODE_SUCCESS) {
                            mDatas = cateResult.getData();
                            OrderUtil.orderCate(mDatas);
                            adapter.notifyDataSetChanged();
                            RuntimeConfig.mainCate = mDatas;
                        } else {
                            ToastUtil.toastByCode(cateResult);
                        }
                    }
                });
            }
        });
    }


    @Override
    public void setStatusBarColor(Activity activity) {

    }


    class CateAdapter extends RecyclerView.Adapter<CateVH> {

        @Override
        public CateVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CateVH(LayoutInflater.from(context).inflate(R.layout.item_fragment_category, parent, false));
        }

        @Override
        public void onBindViewHolder(CateVH holder, int position) {
            holder.setData(mDatas.get(position));

        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }
    }

    class CateVH extends RecyclerView.ViewHolder {
        private ResizableImageView cateImg;

        public CateVH(View itemView) {
            super(itemView);
            cateImg = itemView.findViewById(R.id.cate_img);
        }

        private void setData(final Category c) {
            GlideUtil.GlideWithPlaceHolder(context, c.getImgUrl()).into(cateImg);
            cateImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent categoryIntent = new Intent(getActivity(), CategoryActivity.class);
                    categoryIntent.putExtra(Constant.Key.KEY_CATEGORY_TYPE, c.getName());
                    startActivity(categoryIntent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        }
    }
}
