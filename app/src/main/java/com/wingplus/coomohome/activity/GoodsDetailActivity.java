package com.wingplus.coomohome.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.AddMinusButton;
import com.wingplus.coomohome.component.RadioFlowView;
import com.wingplus.coomohome.component.SelectableTextView;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.component.ToggleViewPager;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.expend.RoundCornersTransformation;
import com.wingplus.coomohome.fragment.good.GoodCommitFragment;
import com.wingplus.coomohome.fragment.good.GoodDetailFragment;
import com.wingplus.coomohome.screenshot.ISnapShotCallBack;
import com.wingplus.coomohome.screenshot.ScreenshotContentObserver;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.NetUtil;
import com.wingplus.coomohome.util.OrderUtil;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.WeixinUtil;
import com.wingplus.coomohome.web.entity.GoodDetail;
import com.wingplus.coomohome.web.entity.GoodSpec;
import com.wingplus.coomohome.web.entity.TypeSpec;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;


/**
 * 商品详情页
 *
 * @author leaffun.
 *         Create on 2017/9/4.
 */
public class GoodsDetailActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<Fragment> al = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();
    private TabLayout mTabs;
    private LinearLayout purchase;
    private ImageView purchase_close;
    private RelativeLayout purchase_back;
    private ToggleViewPager fragPager;
    private RecyclerView purchase_attrs;
    private TextView chosen_attrs;
    private SpannableTextView price_now;
    private TextView stock;
    private TextView dateTip;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private ChangeSKUListener changeSKUListener;
    private InitGoodListener initGoodListener;

    /**
     * 存储用户选中的产品属性
     */
    private LinkedHashMap<String, String> attrKV = new LinkedHashMap<>();

    /**
     * 存储用户选中的数量
     */
    private int num = 1;
    private LinearLayout purchase_content;
    private ImageView purchase_img;
    /**
     * 当前规格
     */
    private GoodSpec gs;

    /**
     * 当前商品所有规格属性
     */
    private List<Map.Entry<String, List<String>>> allAttrs = new ArrayList<>();

    /**
     * 商品规格集
     */
    private List<GoodSpec> specList;

    /**
     * 当前商品
     */
    private GoodDetail gd;

    /**
     * 是否收藏
     */
    private ImageView collect;
    public double[] minAndMax = new double[]{-1, -1};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);

        initView();
        initEvent();

        if (!PermissionUtil.hasPermission(PermissionUtil.READ_EXTERNAL_STORAGE_STR, GoodsDetailActivity.this)) {
            PermissionUtil.applyReadExternalStorage(GoodsDetailActivity.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        ScreenshotContentObserver.startObserve(GoodsDetailActivity.this, new ScreenshotContentObserver.SnapToken() {
            @Override
            public void doSnap(File file) {
                shareImgToWeixin(file);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        FileObserverUtils.setSnapShotCallBack(new SnapShotCallBack(this));
//        FileObserverUtils.startSnapshotWatching();


    }

    @Override
    protected void onPause() {
        super.onPause();

//        FileObserverUtils.stopSnapshotWatching();
//        FileObserverUtils.releaseCallBack();
//        FileObserverUtils.releaseFileObserver();


    }

    @Override
    protected void onStop() {
        super.onStop();
        ScreenshotContentObserver.stopObserve();
    }

    private void initView() {
        mTabs = findViewById(R.id.tool_text);
        fragPager = findViewById(R.id.good_detail_fragment);

        purchase = findViewById(R.id.purchase);
        purchase_close = findViewById(R.id.purchase_close);
        purchase_content = findViewById(R.id.purchase_content);
        purchase_back = findViewById(R.id.purchase_back);
        purchase_img = findViewById(R.id.purchase_img);
        purchase_attrs = findViewById(R.id.purchase_attrs);
        chosen_attrs = findViewById(R.id.chosen_attrs);
        price_now = findViewById(R.id.price_now);
        stock = findViewById(R.id.stock);
        dateTip = findViewById(R.id.date_tip);

        collect = findViewById(R.id.collect);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        purchase_attrs.setLayoutManager(layoutManager);
    }

    private void initEvent() {
        al.add(GoodDetailFragment.newInstance());
        al.add(GoodCommitFragment.newInstance());
        title.add("详情");
        title.add("评价");
        for (int i = 0; i < title.size(); i++) {
            mTabs.addTab(mTabs.newTab().setText(title.get(i)));
        }
        GoodFragmentAdapter adapter = new GoodFragmentAdapter(getSupportFragmentManager(), al, title);
        fragPager.setAdapter(adapter);
        fragPager.setCanScroll(true);
        mTabs.setupWithViewPager(fragPager);

        purchase.setOnClickListener(this);
        purchase_close.setOnClickListener(this);
        purchase_back.setOnClickListener(this);
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                closePurchaseWhenInit();
            }
        };
        purchase.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * 设置属性框的内容
     *
     * @param goodDetail
     */
    public double[] initData(GoodDetail goodDetail) {
        if (initGoodListener != null) {
            initGoodListener.initGood(goodDetail.getId());
        }


        gd = goodDetail;
        specList = gd.getSpecList();
        findGoodSpecByItemNo(gd.getProductId());
        minAndMax = OrderUtil.prepareSpecList(specList, allAttrs);

        if (gd.getFavorite() == Constant.GOOD_COLLECT) {
            GlideUtil.GlideInstance(GoodsDetailActivity.this, R.drawable.icon_lisr_starlight)
                    .apply(new RequestOptions().placeholder(R.drawable.icon_collection))
                    .into(collect);
        }

        if (gs == null) return minAndMax;

        setGoodSpecToView(true, false);
        purchase_attrs.setAdapter(new AttrAdapter());
        return minAndMax;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (fragPager.getCurrentItem() == 0) {
                    GoodsDetailActivity.this.finish();
                } else {
                    fragPager.setCurrentItem(0);
                }

                break;
            case R.id.share:
                if (gd == null) {
                    return;
                }
                shareWebToWeixin(APIConfig.getShareGoodsUrl(gd.getId()));
                break;
//            case R.id.chosen:
//                if (purchasePopup.isShowing()) {
//                    purchasePopup.dismiss();
//                }
//                purchasePopup.showAtLocation(popup, Gravity.BOTTOM, 0, 0);
//                break;
            case R.id.purchase_close:
            case R.id.purchase_back:
                closePurchase();
                break;
//            case R.id.promotion:
//                Intent intent = new Intent(this, PromotionActivity.class);
//                startActivity(intent);
//                break;
            case R.id.customer:
                if (RuntimeConfig.userValid()) {
                    Intent intent1 = new Intent(GoodsDetailActivity.this, CustomerServiceActivity.class);
                    if (gd != null && gd.getImgUrl() != null && gd.getImgUrl().size() > 0) {
                        intent1.putExtra("goodsName", gd.getName());
                        intent1.putExtra("goodsPrice", String.valueOf(gd.getPrice()));
                        intent1.putExtra("goodsImage", gd.getImgUrl().get(0));
                        intent1.putExtra("goodsId", String.valueOf(gd.getId()));
                    }
                    startActivity(intent1);
                } else {
                    Intent login = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                    startActivity(login);
                }
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.cart:
                Intent cart = new Intent(GoodsDetailActivity.this, CartActivity.class);
                startActivity(cart);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.collect:
                if (gd == null) {
                    return;
                }
                if (!RuntimeConfig.userValid()) {
                    Intent login = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                    startActivity(login);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                    return;
                }
                if (gd == null) {
                    return;
                }
                if (gd.getFavorite() == Constant.GOOD_COLLECT) {
                    cancelFavorite();
                } else {
                    addFavorite();
                }
                break;
            case R.id.add_cart:
                if (gd == null) {
                    return;
                }
                if (!RuntimeConfig.userValid()) {
                    Intent login = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                    startActivity(login);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                    return;
                }
                boolean selected = true;
                if (attrKV != null && attrKV.size() > 0) {
                    for (Map.Entry<String, String> entry : attrKV.entrySet()) {
                        if ("".equals(entry.getValue())) {
                            selected = false;
                        }
                    }
                    if (!selected) {
                        showPurchase();
                        return;
                    }
                }
                MallApplication.showProgressDialog(true, GoodsDetailActivity.this);
                APIUtil.addCart(Constant.GoodType.SINGLE, gd.getId(), gs.getProductId(), num, new APIUtil.CallBack<BaseResult>() {
                    @Override
                    public void handleResult(BaseResult result) {
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            ToastUtil.toast("添加购物车成功");
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                        MallApplication.showProgressDialog(false, GoodsDetailActivity.this);
                    }
                });
                break;
            case R.id.buy_now:
                if (gd == null) {
                    return;
                }
                if (!RuntimeConfig.userValid()) {
                    Intent login = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                    startActivity(login);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                    return;
                }
                boolean select = true;
                if (attrKV != null && attrKV.size() > 0) {
                    for (Map.Entry<String, String> entry : attrKV.entrySet()) {
                        if ("".equals(entry.getValue())) {
                            select = false;
                        }
                    }
                    if (!select) {
                        showPurchase();
                        return;
                    }
                }
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_BUY_NOW)
                                , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("type", String.valueOf(Constant.GoodType.SINGLE))
                                        .addParam("id", String.valueOf(gd.getId()))
                                        .addParam("num", String.valueOf(num))
                                        .addParam("productId", gs.getProductId())
                                        .getParams());
                        BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(GoodsDetailActivity.this, CartActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                                }
                            });
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    private void addFavorite() {
        MallApplication.showProgressDialog(true, GoodsDetailActivity.this);
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COLLECTION_ADD),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("id", String.valueOf(gd.getId()))
                                .getParams()
                );
                final StringDataResult stringDataResult = GsonUtil.fromJson(rs, StringDataResult.class);
                MallApplication.showProgressDialog(false, GoodsDetailActivity.this);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringDataResult != null && stringDataResult.getResult() == APIConfig.CODE_SUCCESS) {
                            gd.setFavorite(Constant.GOOD_COLLECT);
                            GlideUtil.GlideInstance(GoodsDetailActivity.this, R.drawable.icon_lisr_starlight)
                                    .apply(new RequestOptions().placeholder(R.drawable.icon_collection))
                                    .into(collect);
                            ToastUtil.toast(getApplicationContext(), stringDataResult.getMessage());
                        } else {
                            ToastUtil.toastByCode(stringDataResult);
                        }
                    }
                });
            }
        });
    }

    private void cancelFavorite() {
        MallApplication.showProgressDialog(true, GoodsDetailActivity.this);
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COLLECTION_DELETE)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("id", String.valueOf(gd.getId()))
                                .getParams());
                final StringDataResult stringDataResult = GsonUtil.fromJson(rs, StringDataResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        MallApplication.showProgressDialog(false, GoodsDetailActivity.this);
                        if (stringDataResult != null && stringDataResult.getResult() == APIConfig.CODE_SUCCESS) {
                            ToastUtil.toast("取消收藏成功");
                            GlideUtil.GlideInstance(GoodsDetailActivity.this, R.drawable.icon_collection)
                                    .apply(new RequestOptions().placeholder(R.drawable.icon_collection))
                                    .into(collect);
                            gd.setFavorite(Constant.GOOD_COLLECT_NO);
                        } else {
                            ToastUtil.toastByCode(stringDataResult);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions,
                                           int[] grantResults) {
        switch (permsRequestCode) {
            case PermissionUtil.READ_EXTERNAL_STORAGE_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    //用户成功授权
                    if (!PermissionUtil.hasPermission(PermissionUtil.READ_EXTERNAL_STORAGE_STR, GoodsDetailActivity.this)) {
                        ToastUtil.toast(this, "缺少存储权限，可能影响截屏分享功能");
                    }
                } else {
                    //用户拒绝授权
                    ToastUtil.toast(this, "缺少存储权限，可能影响截屏分享功能");
                    PermissionUtil.goPermissionManager(GoodsDetailActivity.this);
                }
                break;
            default:
                ToastUtil.toast(permsRequestCode + "");
                break;
        }
    }

    /**
     * ------------------商品详情、评论Fragment适配器
     */
    private class GoodFragmentAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragment;
        private ArrayList<String> title;

        GoodFragmentAdapter(FragmentManager fm, ArrayList<Fragment> l, ArrayList<String> ls) {
            super(fm);
            fragment = l;
            title = ls;
        }

        @Override
        public Fragment getItem(int position) {
            return fragment.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }
    }

    /**
     * --------------------属性选择适配器--------------
     */
    private class AttrAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new AttrViewHolder(LayoutInflater.from(GoodsDetailActivity.this).inflate(R.layout.item_good_detail_purchase_attr, parent, false));
            } else if (viewType == 1) {
                return new NumViewHolder(LayoutInflater.from(GoodsDetailActivity.this).inflate(R.layout.item_good_detail_purchase_count, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (position < getItemCount() - 1 && holder instanceof AttrViewHolder) {
                if (allAttrs != null && allAttrs.size() > 0) {
                    AttrViewHolder attrVH = (AttrViewHolder) holder;
                    final String key = allAttrs.get(position).getKey();
                    attrVH.attrName.setText(key);
                    String nowAttr = "";
//                    List<TypeSpec> specs = gs.getSpec();
//                    for (TypeSpec typeSpec : specs) {
//                        if (typeSpec.getType().equals(allAttrs.get(position).getKey())) {
//                            nowAttr = typeSpec.getValue();
//                        }
//                    }
                    attrVH.attrValues.initData(allAttrs.get(position).getValue(), nowAttr);
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
            return allAttrs.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return (position == getItemCount() - 1) ? 1 : 0;
        }
    }

    //------------属性选择Holder---------------------

    private class AttrViewHolder extends RecyclerView.ViewHolder {
        private TextView attrName;
        private RadioFlowView attrValues;

        AttrViewHolder(View itemView) {
            super(itemView);
            attrName = itemView.findViewById(R.id.attr_name);
            attrValues = itemView.findViewById(R.id.attr_values);
        }
    }

    private class NumViewHolder extends RecyclerView.ViewHolder {
        private AddMinusButton addMinusButton;

        NumViewHolder(View itemView) {
            super(itemView);
            addMinusButton = itemView.findViewById(R.id.add_minus);
            addMinusButton.setNumberUpdateListener(new AddMinusButton.NumberUpdateListener() {
                @Override
                public void numberUpdate(int number, int changeVal) {
                    num = number;
                    addMinusButton.initNumber(num);
                }
            });
        }
    }


    //-------------------------属性选择框 开关------------------

    public void showPurchase() {
        purchase.setVisibility(View.VISIBLE);
        purchase.getBackground().setAlpha(255);
        purchase_content.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        ObjectAnimator animator;
        float purchaseHeight = purchase_content.getMeasuredHeight();
        animator = ObjectAnimator.ofFloat(purchase_content, "translationY", purchaseHeight, 0);
        animator.setDuration(500);
        animator.start();
    }

    private void closePurchase() {
        ObjectAnimator animator;
        float purchaseHeight = purchase_content.getMeasuredHeight();
        animator = ObjectAnimator.ofFloat(purchase_content, "translationY", 0, purchaseHeight);
        animator.setDuration(300);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                purchase.getBackground().setAlpha(0);
                purchase.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void closePurchaseWhenInit() {
        ObjectAnimator animator;
        float purchaseHeight = purchase_content.getMeasuredHeight();
        animator = ObjectAnimator.ofFloat(purchase_content, "translationY", 0, purchaseHeight);
        animator.setDuration(1);
        animator.start();
        purchase.getBackground().setAlpha(0);
        purchase.setVisibility(View.GONE);
    }

    /**
     * 用户选择了属性
     *
     * @param key
     * @param choseAttr
     */
    private boolean changeAttr(String key, String choseAttr) {
        boolean needChange = true;
        if (choseAttr.equals(attrKV.get(key))) {
            //没有改变
            needChange = false;
        } else {
            attrKV.put(key, choseAttr);
            String attrStr = "";
            for (Map.Entry<String, String> entry : attrKV.entrySet()) {
                if ("".equals(entry.getValue())) {
                    needChange = false;
                } else {
                    attrStr += (entry.getValue() + " ");
                }
            }

            if (changeSKUListener != null) {
                changeSKUListener.setAttr(attrStr);
            }


            if (!needChange) {
                setGoodSpecToView(false, false);
                //属性选择未选中全部属性分类,不再寻找规格
                return true;
            }

            String temp = attrKV.get(key);
            attrKV.put(key, choseAttr);

            //通过attr查找itemNo
            //找到itemNo，更换价格，存货信息
            String itemNo = "";
            for (GoodSpec goodSpec : specList) {
                int i = 0;
                for (TypeSpec ts : goodSpec.getSpec()) {
                    String type = ts.getType();
                    if (ts.getValue().equals(attrKV.get(type))) {
                        i++;
                    }
                }
                if (i == goodSpec.getSpec().size()) {
                    itemNo = goodSpec.getProductId();
                    break;
                }
            }


            if (needChange = findGoodSpecByItemNo(itemNo)) {
                setGoodSpecToView(false, true);
                //fragment通信
                if (changeSKUListener != null) {
                    changeSKUListener.setAttr("");
                    changeSKUListener.changeSKU(gs);
                }
            } else {
                attrKV.put(key, "");
                ToastUtil.toast(getApplicationContext(), "抱歉！暂无该规格");
            }
        }
        return needChange;
    }

    public interface ChangeSKUListener {
        void changeSKU(GoodSpec goodSpec);

        void setAttr(String attrStr);
    }

    public void setChangeSKUListener(ChangeSKUListener changeSKUListener) {
        this.changeSKUListener = changeSKUListener;
    }

    public interface InitGoodListener {
        void initGood(long goodId);
    }

    public void setInitListener(InitGoodListener initGoodListener) {
        this.initGoodListener = initGoodListener;
    }


    /**
     * 初始化当前规格
     *
     * @param itemNo
     */
    private boolean findGoodSpecByItemNo(String itemNo) {
        for (GoodSpec spec : specList) {
            if (spec.getProductId().equals(itemNo)) {
                gs = spec;
                return true;
            }
        }
        return false;
    }

    /**
     * 设置商品信息(价格、属性)
     */
    private void setGoodSpecToView(boolean first, boolean choseAllAttr) {
        List<TypeSpec> spec = gs.getSpec();
        String purchase_img_url = "";
        for (int i = 0; i < spec.size(); i++) {
            if (first) {
                attrKV.put(spec.get(i).getType(), "");
            }
            if (choseAllAttr && spec.get(i).getImage() != null && !spec.get(i).getImage().isEmpty() && spec.get(i).getValue().equals(attrKV.get(spec.get(i).getType()))) {
                purchase_img_url = spec.get(i).getImage();
            }
        }
        chosen_attrs.setText(first ? "请选择规格、数量" : StringUtil.bindStr(attrKV));

        if (minAndMax[0] != -1 && minAndMax[1] != -1 && !choseAllAttr) {
            price_now.setPreString(getResources().getString(R.string.currency_symbol));
            price_now.setText(PriceFixUtil.format(minAndMax[0]) + " ~ " + PriceFixUtil.format(minAndMax[1]));
        } else {
            price_now.setPreString("");
            price_now.setText(PriceFixUtil.format(gs.getPrice()));
        }


        if (first || choseAllAttr) {
            GlideUtil.GlideWithPlaceHolder(GoodsDetailActivity.this, purchase_img_url.isEmpty() ? gd.getImgUrl().get(0) : purchase_img_url)
                    .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(GoodsDetailActivity.this, UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                    .into(purchase_img);
        }
        if (gs.getStock() > 0) {
            stock.setText("有货");
            dateTip.setText("在16:00前下单，预计3日内送达。");
        } else {
            stock.setText("无货");
            dateTip.setText("");
        }
    }


    //--------------------------分享截屏相关---------------------------

    private void shareImgToWeixin(final File file) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!NetUtil.IsActivityNetWork(GoodsDetailActivity.this)) {
                        ToastUtil.toast("请检查您的网络连接");
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsDetailActivity.this);
                    builder.setTitle("即将跳转微信");
                    builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getGoodsQR(file);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

//            ToastUtil.toast("即将跳转微信");
//            getGoodsQR(file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private Bitmap drawImage(File file, Bitmap qrCode) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Bitmap sharePre = BitmapFactory.decodeResource(getResources(), R.drawable.share_qr_pre);

        Bitmap qr = Bitmap.createScaledBitmap(qrCode, 300, 300, false);


        int qrWidth = qr.getWidth();
        int qrHeight = qr.getHeight();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        int scaleWidth = sharePre.getWidth();
        if (scaleWidth + qrWidth > width) {
            scaleWidth = width - qrWidth;
        } else if (scaleWidth + qrWidth < width) {
            scaleWidth = sharePre.getWidth() * 300 / sharePre.getHeight();
        }
        sharePre = Bitmap.createScaledBitmap(sharePre, scaleWidth, 300, false);

        Bitmap newb = Bitmap.createBitmap(width, height + qrHeight, Bitmap.Config.ARGB_8888);

        Canvas cv = new Canvas(newb);
        cv.drawBitmap(bitmap, 0, 0, null);

        Paint backPaint = new Paint();
        backPaint.setColor(Color.WHITE);
        cv.drawRect(0, height, width, height + qrHeight, backPaint);

        //居中绘制
        cv.drawBitmap(sharePre, (width - qrWidth - scaleWidth) / 2, height, null);
        //靠右绘制
        cv.drawBitmap(qr, width - qrWidth, height, null);


//        Bitmap newb = Bitmap.createBitmap(width, height+qrHeight, Bitmap.Config.ARGB_8888);
//
//        Canvas cv = new Canvas(newb);
//        cv.drawBitmap(bitmap, 0, 0, null);
//
//        Paint backPaint = new Paint();
//        backPaint.setColor(Color.WHITE);
//        cv.drawRect(0, height, width, height + qrHeight, backPaint);
//
//        cv.drawBitmap(qr, width-qrWidth, height, null);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setTypeface(Typeface.DEFAULT_BOLD);
//        paint.setTextSize(40);
//        paint.setColor(UIUtils.getColor(R.color.titleBlack));
//        cv.drawText("长按图片打开Coomo Home APP查看详情!", 80, height + qrHeight/2, paint);


//          二维码在左边，覆盖渐变
//        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//        Canvas cv = new Canvas(newb);
//        cv.drawBitmap(bitmap, 0, 0, null);
//
//        Paint backPaint = new Paint();
//        backPaint.setARGB(255, 255, 255, 255);
//        Shader mShader = new LinearGradient(width/2, height - qrHeight, width/2, height, new int[]{Color.argb(125,255,255,255),Color.argb(255,255,255,255)}, null, Shader.TileMode.REPEAT);
//        backPaint.setShader(mShader);
//        cv.drawRect(0, height - qrHeight, width, height, backPaint);
//
//        cv.drawBitmap(qr, 0, height - qrHeight, null);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setTypeface(Typeface.DEFAULT_BOLD);
//        paint.setTextSize(36);
//        paint.setColor(UIUtils.getColor(R.color.titleBlack));
//        cv.drawText("长按图片打开Coomo Home APP查看详情!", qrWidth + 20, height - qrHeight/2, paint);

        return newb;
    }

    private void shareWebToWeixin(String webUrl) {

        if (!MallApplication.mWxApi.isWXAppInstalled()) {
            ToastUtil.toast("您还未安装微信客户端");
            return;
        }

        if (!NetUtil.IsActivityNetWork(GoodsDetailActivity.this)) {
            ToastUtil.toast("请检查您的网络连接");
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webUrl;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "CoomoHome";
        msg.description = gd.getName();
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.img_customeravatar);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage";
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        MallApplication.mWxApi.sendReq(req);
    }

    private void getGoodsQR(final File file) {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap;
                try {
                    String content = APIConfig.SHARE_SERVER_PRE + gd.getId() + APIConfig.SHARE_SERVER_AFTER;
                    LogUtil.i("分享二维码连接：", content);
                    bitmap = QRCodeEncoder.syncEncodeQRCode(content, 300);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ToastUtil.toast("分享二维码生成失败");
                    return;
                }

                if (bitmap == null) {
                    return;
                }

                try {
                    Bitmap bmp = drawImage(file, bitmap);

                    WXImageObject imageObject = new WXImageObject(bmp);
                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = imageObject;

                    Bitmap thumb = Bitmap.createScaledBitmap(bmp, 300, 300, true);
                    bmp.recycle();
                    msg.setThumbImage(thumb);

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = "img";
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneSession;

                    MallApplication.mWxApi.sendReq(req);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ToastUtil.toast("分享商品失败");
                }
            }
        });
    }

    public void saveBitmapFile(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SnapShotCallBack implements ISnapShotCallBack {

        private Context context;

        SnapShotCallBack(Context context) {
            this.context = context;
        }

        @Override
        public void snapShotTaken(String path) {
            try {
                LogUtil.i("截屏", "捕捉到截屏事件");

                File file = new File(path);
                shareImgToWeixin(file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
