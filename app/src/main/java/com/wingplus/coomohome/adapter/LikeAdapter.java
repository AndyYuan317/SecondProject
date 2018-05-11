package com.wingplus.coomohome.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.LoginActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.component.SquareImage;
import com.wingplus.coomohome.component.StrikeTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.result.BaseResult;

import java.util.List;

/**
 * 猜你喜欢商品适配器
 */
public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.GoodViewHolder> {

    private List<GoodShow> mLike;
    private Activity activity;

    public LikeAdapter(List<GoodShow> mLike, Activity activity) {
        this.mLike = mLike;
        this.activity = activity;
    }

    public void setData(List<GoodShow> list){
        mLike = list;
    }

    @Override
    public GoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_category_grid_good, parent, false), activity);
    }

    @Override
    public void onBindViewHolder(GoodViewHolder holder, int position) {
        holder.setData(mLike.get(position));
    }

    @Override
    public int getItemCount() {
        return mLike == null ? 0 : mLike.size();
    }


    class GoodViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout good;
        private SquareImage good_img;
        private TextView good_title;
        private TextView good_intro;
        private SpannableTextView good_price;
        private StrikeTextView good_old_price;
        private TextView good_label;
        private ImageView add_cart;

        private GoodShow gs;
        private Activity context;

        GoodViewHolder(View itemView, final Activity context) {
            super(itemView);

            this.context = context;

            good = itemView.findViewById(R.id.good_1);
//            endLine = itemView.findViewById(R.id.end_line);
            good_img = itemView.findViewById(R.id.good_img);
            good_title = itemView.findViewById(R.id.good_title);
            good_intro = itemView.findViewById(R.id.good_intro);
            good_price = itemView.findViewById(R.id.good_price);
            good_old_price = itemView.findViewById(R.id.good_old_price);
            good_label = itemView.findViewById(R.id.good_label);
            add_cart = itemView.findViewById(R.id.good_add_cart);
            good_img.setBackground(context.getResources().getDrawable(R.drawable.bg_text_rectangle_border_graylight_2));

            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(gs.getId()));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
            add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!RuntimeConfig.userValid()){
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        return;
                    }
                    MallApplication.showProgressDialog(true, context);
                    APIUtil.addCart(Constant.GoodType.SINGLE, gs.getId(), gs.getProductId(), 1, new APIUtil.CallBack<BaseResult>() {
                        @Override
                        public void handleResult(BaseResult result) {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                ToastUtil.toast("添加购物车成功");
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                            MallApplication.showProgressDialog(false, context);
                        }
                    });
                }
            });
        }

        public void setData(GoodShow good) {
            GlideUtil.GlideWithPlaceHolder(context, good.getImgUrl().get(0)).into(good_img);
            good_title.setText(good.getName());
            good_intro.setText(good.getDescription());
            good_price.setText(PriceFixUtil.format(good.getPrice()));
            if(PriceFixUtil.checkNeedScribing(good.getPrice(), good.getOriginalPrice())){
                good_old_price.setVisibility(View.VISIBLE);
                good_old_price.setText(PriceFixUtil.format(good.getOriginalPrice()));
            }else{
                good_old_price.setVisibility(View.INVISIBLE);
            }
            if (good.getTag() != null && good.getTag().size() > 0) {
                good_label.setVisibility(View.VISIBLE);
                good_label.setText(good.getTag().get(0));
            } else {
                good_label.setVisibility(View.INVISIBLE);
            }

            gs = good;
        }
    }

}
