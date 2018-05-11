package com.wingplus.coomohome.fragment.coupon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.BaseFragment;
import com.wingplus.coomohome.util.DisplayUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.ViewUtils;
import com.wingplus.coomohome.view.LoadingPage;
import com.wingplus.coomohome.web.entity.Coupon;

import java.util.List;

/**
 * 页面加载完成，即开始请求数据，无刷新机制
 *
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public abstract class CouponBaseFragment extends BaseFragment {


    public LoadingPage loadingPage;
    public Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        if (loadingPage == null) {
            loadingPage = new LoadingPage(getActivity()) {
                @Override
                public LoadResult load() {
                    return CouponBaseFragment.this.load(); // 将加载数据方法开放给子类
                }

                @Override
                public View createSuccessLoad() {
                    return CouponBaseFragment.this.createSuccessLoad();
                }

                @Override
                protected void refreshUIInHere() {
                    refreshUI();
                }

                @Override
                public void setEmptyImage(ImageView img) {
                    GlideUtil.GlideInstance(context, R.drawable.icon_nocoupon).into(img);
                }
            };
        } else {
            ViewUtils.removeParent(loadingPage);
        }

        return loadingPage;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    /**
     * 请求数据 并刷新页面
     */
    public void loadData() {
        if (loadingPage != null) {
            loadingPage.showState();
        }
    }

    /**
     * 请求数据，返回枚举结果
     *
     * @return
     */
    public abstract LoadingPage.LoadResult load();

    /**
     * 用数据来渲染布局
     *
     * @return
     */
    public abstract View createSuccessLoad();

    public void refreshUI(){

    }

    class CouponAdapter extends RecyclerView.Adapter<CouponVH> {

        private List<Coupon> cs;

        public CouponAdapter(List<Coupon> couponList) {
            this.cs = couponList;
        }

        public void setData(List<Coupon> couponList) {
            this.cs = couponList;
        }

        @Override
        public CouponVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CouponVH(LayoutInflater.from(context).inflate(R.layout.vh_fragment_coupon, parent, false));
        }

        @Override
        public void onBindViewHolder(CouponVH holder, int position) {
            holder.setData(cs.get(position));
            int margin;
            if (position == 0) {
                margin = 20;
            } else {
                margin = 7;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.itemView.getLayoutParams());
            params.setMargins(DisplayUtil.dip2px(context, 16), DisplayUtil.dip2px(context, margin), DisplayUtil.dip2px(context, 16), DisplayUtil.dip2px(context, 7));
            holder.itemView.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return cs == null ? 0 : cs.size();
        }
    }

    class CouponVH extends RecyclerView.ViewHolder {

        private final TextView money;
        private final TextView couponRemark;
        private final TextView validRemark;
        private final TextView timeRemark;
        private final TextView useNow;
        private final ImageView status;

        public CouponVH(View itemView) {
            super(itemView);
            money = itemView.findViewById(R.id.money);
            couponRemark = itemView.findViewById(R.id.coupon_remark);
            validRemark = itemView.findViewById(R.id.valid_remark);
            timeRemark = itemView.findViewById(R.id.time_remark);
            useNow = itemView.findViewById(R.id.use_now);
            status = itemView.findViewById(R.id.status);
        }

        public void setData(Coupon coupon) {
            String s = coupon.getPrice() + "";
            int v = (int) (coupon.getPrice() * 1000 % 1000);
            final boolean contains = s.contains(".");
            if (contains && v == 0) {
                s = s.substring(0, s.indexOf("."));
            }
            money.setText(s);
            couponRemark.setText(coupon.getName());
            validRemark.setText(coupon.getDescription());
            timeRemark.setText(coupon.getStartDate() + "至" + coupon.getEndDate());

            if (coupon.getState() == Constant.CouponStatus.FRESH) {
                useNow.setBackgroundColor(UIUtils.getColor(R.color.coupon_use_back_ac));
                useNow.setTextColor(UIUtils.getColor(R.color.colorPrimary));
                useNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                        RuntimeConfig.tabGroup.changeSelected(1);
                    }
                });
                status.setVisibility(View.GONE);
            } else if (coupon.getState() == Constant.CouponStatus.ALREADY) {
                useNow.setBackgroundColor(UIUtils.getColor(R.color.coupon_use_back));
                useNow.setTextColor(UIUtils.getColor(R.color.gray_light));
                status.setVisibility(View.VISIBLE);
                GlideUtil.GlideInstance(context, R.drawable.already).into(status);
            } else {
                useNow.setBackgroundColor(UIUtils.getColor(R.color.coupon_use_back));
                useNow.setTextColor(UIUtils.getColor(R.color.gray_light));
                status.setVisibility(View.VISIBLE);
                GlideUtil.GlideInstance(context, R.drawable.invalid).into(status);
            }
        }
    }
}
