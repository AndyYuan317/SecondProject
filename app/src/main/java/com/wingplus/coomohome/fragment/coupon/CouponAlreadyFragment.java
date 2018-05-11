package com.wingplus.coomohome.fragment.coupon;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.web.entity.Coupon;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.view.LoadingPage;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.CouponResult;

import java.util.List;

/**
 * 已使用的优惠券
 *
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public class CouponAlreadyFragment extends CouponBaseFragment {


    private List<Coupon> cs;


    @Override
    public LoadingPage.LoadResult load() {
        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COUPON_LIST),
                new ParamsBuilder()
                        .addParam("token", RuntimeConfig.user.getToken())
                        .addParam("state", String.valueOf(Constant.CouponStatus.ALREADY))
                        .getParams());
        CouponResult couponResult = GsonUtil.fromJson(rs, CouponResult.class);
        if (couponResult == null || couponResult.getResult() != APIConfig.CODE_SUCCESS) {
            ToastUtil.toastByCode(couponResult);
            return LoadingPage.LoadResult.empty;
        } else if (couponResult.getResult() == APIConfig.CODE_SUCCESS && couponResult.getData().size() <= 0) {
            return LoadingPage.LoadResult.empty;
        } else {
            cs = couponResult.getData();
            return LoadingPage.LoadResult.success;
        }
    }

    @Override
    public View createSuccessLoad() {
        View all = LayoutInflater.from(context).inflate(R.layout.fragment_after_sale, loadingPage, false);
        RecyclerView rv = all.findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        rv.setAdapter(new CouponAdapter(cs));

        MyRefreshLayout tr = all.findViewById(R.id.tr);
        tr.forbidden();
        return all;
    }
}
