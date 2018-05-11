package com.wingplus.coomohome.component;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.expend.RoundCornersTransformation;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.OrderUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodDetail;
import com.wingplus.coomohome.web.entity.GoodSpec;
import com.wingplus.coomohome.web.entity.TypeSpec;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.GoodDetailResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品属性选择框
 *
 * @author leaffun.
 *         Create on 2017/11/6.
 */
public class PurchaseDialog extends Dialog {
    private LinearLayout purchase;
    private ImageView purchase_close;
    private LinearLayout purchase_content;
    private RelativeLayout purchase_back;
    private ImageView purchase_img;
    private RecyclerView purchase_attrs;
    private TextView chosen_attrs;
    private TextView price_now;
    private TextView stock;
    private TextView dateTip;
    private TextView change;

    private Context context;
    private final InitGood initGood;

    private long goodId;
    private String productId;
    private GoodDetail goodDetail;

    /**
     * 选中的商品规格
     */
    private GoodSpec gs;
    /**
     * 当前商品所有规格属性
     */
    private List<Map.Entry<String, List<String>>> allAttrs = new ArrayList<>();
    /**
     * 存储用户选中的产品属性
     */
    private LinkedHashMap<String, String> attrKV = new LinkedHashMap<>();
    private String newProductId;

    public PurchaseDialog(@NonNull Context context, long goodId, String productId, InitGood initGood) {
        super(context, R.style.CustomDialog);

        this.context = context;
        this.goodId = goodId;
        this.productId = productId;
        this.initGood = initGood;
        initView();
        initData();
    }

    private void initView() {
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_purchase, null);

        purchase = mView.findViewById(R.id.purchase);
        purchase_close = mView.findViewById(R.id.purchase_close);
        purchase_content = mView.findViewById(R.id.purchase_content);
        purchase_back = mView.findViewById(R.id.purchase_back);
        purchase_img = mView.findViewById(R.id.purchase_img);
        purchase_attrs = mView.findViewById(R.id.purchase_attrs);
        chosen_attrs = mView.findViewById(R.id.chosen_attrs);
        price_now = mView.findViewById(R.id.price_now);
        stock = mView.findViewById(R.id.stock);
        dateTip = mView.findViewById(R.id.date_tip);
        change = mView.findViewById(R.id.change);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setWindowAnimations(R.style.purchaseAnim);
            window.setGravity(Gravity.BOTTOM);
        }
        setContentView(mView);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.purchase_back:
                    case R.id.purchase_close:
                        dismiss();
                        break;
                    case R.id.change:
                        if (initGood != null) {
                            if(newProductId == null || newProductId.equals(productId)){
                                return;
                            }
                            initGood.changeSpec(goodId, productId, newProductId);
                        }
                        dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        purchase_back.setOnClickListener(listener);
        purchase_close.setOnClickListener(listener);
        change.setOnClickListener(listener);



        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        purchase_attrs.setLayoutManager(layoutManager);
    }

    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_DETAIL),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("id", "" + goodId)
                                .getParams());
                final GoodDetailResult goodDetailResult = GsonUtil.fromJson(rs, GoodDetailResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (goodDetailResult != null && goodDetailResult.getResult() == APIConfig.CODE_SUCCESS) {
                            goodDetail = goodDetailResult.getData();
                            gs = null;
                            if (goodDetail != null && goodDetail.getImgUrl() != null) {

                                for (GoodSpec spec : goodDetail.getSpecList()) {
                                    if (spec.getProductId().equals(productId)) {
                                        gs = spec;
                                        break;
                                    }
                                }

                                OrderUtil.prepareSpecList(goodDetail.getSpecList(), allAttrs);

                                if (gs != null) {
                                    setGoodSpecToDialog();
                                    purchase_attrs.setAdapter(new AttrAdapter());
                                }
                            }
                        } else {
                            ToastUtil.toastByCode(goodDetailResult);
                        }
                    }
                });
            }
        });
    }

    /**
     * --------------------属性选择适配器--------------
     */
    private class AttrAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new AttrViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_purchase_attr, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof AttrViewHolder) {
                if (allAttrs != null && allAttrs.size() > 0) {
                    AttrViewHolder attrVH = (AttrViewHolder) holder;
                    Map.Entry<String, List<String>> entry = allAttrs.get(position);//当前规格组
                    final String key = entry.getKey();
                    attrVH.attrName.setText(key);
                    List<TypeSpec> nowSpecs = gs.getSpec();//商品规格
                    String nowAttr = "";//商品在当前规格组中的值
                    for (TypeSpec typeSpec : nowSpecs) {
                        if (typeSpec.getType().equals(entry.getKey())) {
                            nowAttr = typeSpec.getValue();
                        }
                    }
                    attrVH.attrValues.initData(entry.getValue(), nowAttr);
                    attrVH.attrValues.setSelectedListener(new RadioFlowView.SelectedListener() {
                        @Override
                        public boolean selected(SelectableTextView stv) {
                            return changeAttr(key, stv.getText().toString());
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return allAttrs == null ? 0 : allAttrs.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    private class AttrViewHolder extends RecyclerView.ViewHolder {
        private TextView attrName;
        private RadioFlowView attrValues;

        AttrViewHolder(View itemView) {
            super(itemView);
            attrName = itemView.findViewById(R.id.attr_name);
            attrValues = itemView.findViewById(R.id.attr_values);
        }
    }

    /**
     * 设置商品信息(价格、属性)
     */
    private void setGoodSpecToDialog() {
        List<TypeSpec> spec = gs.getSpec();
        String purchase_img_url = "";
        for (int i = 0; i < spec.size(); i++) {
            attrKV.put(spec.get(i).getType(), spec.get(i).getValue());
            if (spec.get(i).getImage() != null && !spec.get(i).getImage().isEmpty()) {
                purchase_img_url = spec.get(i).getImage();
            }
        }
        chosen_attrs.setText(StringUtil.bindStr(attrKV));
        price_now.setText(PriceFixUtil.format(gs.getPrice()));

        GlideUtil.GlideWithPlaceHolder(context, purchase_img_url.isEmpty() ? goodDetail.getImgUrl().get(0) : purchase_img_url)
                .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(context, UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                .into(purchase_img);

        if (gs.getStock() > 0) {
            stock.setText("有货");
            dateTip.setText("在16:00前下单，预计3日内送达。");
        } else {
            stock.setText("无货");
            dateTip.setText("");
        }
    }

    /**
     * 用户选择了属性
     *
     * @param key
     * @param choseAttr
     */
    private boolean changeAttr(String key, String choseAttr) {
        boolean needChange = false;
        if (!choseAttr.equals(attrKV.get(key))) {
            String temp = attrKV.get(key);//保存原先key-value
            attrKV.put(key, choseAttr);//替入新value

            //通过attr查找productId
            //找到productId，更换价格，存货信息
            for (GoodSpec goodSpec : goodDetail.getSpecList()) {
                int i = 0;
                for (TypeSpec ts : goodSpec.getSpec()) {
                    String type = ts.getType();
                    if (attrKV.get(type).equals(ts.getValue())) {
                        i++;
                    }
                }
                if (i == goodSpec.getSpec().size()) {//所有规格组都相同
                    gs = goodSpec;
                    newProductId = goodSpec.getProductId();
                    needChange = (goodSpec.getStock() > 0);
                    break;
                }
            }

            if (needChange) {
                setGoodSpecToDialog();//更新dialog
            } else {
                attrKV.put(key, temp);//恢复原先的key-value
                ToastUtil.toast("该规格暂无库存");
            }
        }
        return needChange;
    }


    public interface InitGood {
        void changeSpec(long goodId, String oldProductId, String newProductId);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = RuntimeConfig.SCREEN_WIDTH;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }
}
