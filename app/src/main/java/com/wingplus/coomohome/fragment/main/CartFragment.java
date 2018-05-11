package com.wingplus.coomohome.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.CartActivity;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.LoginActivity;
import com.wingplus.coomohome.activity.OrderAddressEditActivity;
import com.wingplus.coomohome.activity.OrderMakeActivity;
import com.wingplus.coomohome.adapter.LikeVH;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.AddMinusButton;
import com.wingplus.coomohome.component.PurchaseDialog;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.BaseFragment;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.CartData;
import com.wingplus.coomohome.web.entity.CartOfActivity;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.CartResult;
import com.wingplus.coomohome.web.result.GuessLikeResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 购物车
 *
 * @author leaffun.
 *         Create on 2017/8/26.
 */
public class CartFragment extends BaseFragment {

    public static final String ACTIVITY_TYPE_DAILY = "daily";
    public static final String ACTIVITY_TYPE_COMBIND = "combind";
    public static final String ACTIVITY_TYPE_SPECIAL = "special";


    private LayoutInflater myInflater;
    private View rootView;
    private TwinklingRefreshLayout tr;
    private Context context;
    private SpannableTextView allTotal;
    private CheckBox allSelect;
    private TextView control;
    private TextView makeOrder;
    private TextView deleteCart;
    private LinearLayout controlOrder;
    private RelativeLayout controlDelete;


    private CartAdapter adapter;

    /**
     * 猜你喜欢商品数据
     */
    private List<GoodShow> mLike;

    /**
     * 购物车商品数据
     */
    private CartData mCartData;

    /**
     * 处于编辑模式
     */
    private boolean inEditing = false;

    /**
     * 编辑模式下的选中的id_productId
     */
    private HashMap<Long, HashSet<String>> editChecked = new HashMap<>();
    private LinearLayout toLogin;


    public CartFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflater = inflater;
        context = getContext();
        if (rootView == null) {
            rootView = myInflater.inflate(R.layout.fragment_main_cart, container, false);
        }
        initView();
        initEvent();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        RuntimeConfig.registerCartFragment(this);
        initData();
        if (inEditing) {
            doDone();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RuntimeConfig.unRegisterCartFragment(this);
    }

    private void initView() {
        //控制返回箭头的显隐
        ImageView cartBack = rootView.findViewById(R.id.cart_back);
        cartBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            }
        });
        cartBack.setVisibility(getActivity() != null && getActivity() instanceof CartActivity ? View.VISIBLE : View.GONE);

        tr = rootView.findViewById(R.id.tr);
        toLogin = rootView.findViewById(R.id.toLogin);

        RecyclerView rv = rootView.findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new CartAdapter();
        rv.setAdapter(adapter);

        allTotal = rootView.findViewById(R.id.all_total);
        allSelect = rootView.findViewById(R.id.all_select);
        makeOrder = rootView.findViewById(R.id.make_order);
        deleteCart = rootView.findViewById(R.id.delete_cart);
        control = rootView.findViewById(R.id.control);
        controlOrder = rootView.findViewById(R.id.control_order);
        controlDelete = rootView.findViewById(R.id.control_delete);
    }

    private void initEvent() {
        allSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean checked = ((CheckBox) v).isChecked();
                selectAllData(checked);
            }
        });

        makeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrder();
            }
        });

        deleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDelete();
            }

        });

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = control.getText().toString();
                if ("编辑".equals(string)) {
                    doEdit();
                } else {
                    doDone();
                }
            }

        });

        tr.setAutoLoadMore(false);
        tr.setEnableLoadmore(false);
        tr.setEnableOverScroll(false);
        tr.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                if (RuntimeConfig.userValid()) {
                    initData();
                } else {
                    tr.finishRefreshing();
                }
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {

            }
        });
    }

    private void initData() {
        if (!RuntimeConfig.userValid()) {
            toLogin.setVisibility(View.VISIBLE);
            toLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        } else {
            toLogin.setVisibility(View.GONE);


            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_INFO)
                            , new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .getParams());
                    final CartResult result = GsonUtil.fromJson(rs, CartResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            tr.finishRefreshing();
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                mCartData = result.getData();
                                updateCart();
                                RuntimeConfig.notifyCartNum();
                            } else {
                                if (result == null || result.getResult() != APIConfig.CODE_AUTHENTICATION_ERR) {
                                    ToastUtil.toastByCode(result);
                                }
                            }
                        }
                    });
                }
            });
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_GUESS)
                        , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken()).getParams());
                GuessLikeResult result = GsonUtil.fromJson(rs, GuessLikeResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    mLike = result.getData();
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });


        updateOrderPrice();
    }


    @Override
    public void setStatusBarColor(Activity activity) {

    }

    private class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        CartAdapter() {

        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 2) {
                //猜你喜欢
                return new LikeVH(LayoutInflater.from(context).inflate(R.layout.item_fragment_cart_like, parent, false), getActivity(), null);
            } else if (viewType == 1) {
                //空购物车
                return new BlankVH(LayoutInflater.from(context).inflate(R.layout.item_fragment_cart_blank, parent, false));
            }
            return new CartVH(LayoutInflater.from(context).inflate(R.layout.item_fragment_cart, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CartVH) {
                CartVH cartVH = (CartVH) holder;
                cartVH.setData(position);
            } else if (holder instanceof BlankVH) {
                BlankVH blankVH = (BlankVH) holder;
                blankVH.setVisibility(mCartData == null || (mCartData.getCommon().size() == 0 && mCartData.getActivity().size() == 0));
            } else if (holder instanceof LikeVH) {
                ((LikeVH) holder).refreshLikeData(mLike);
            }

        }

        @Override
        public int getItemCount() {
            int base = 2;
            if (mCartData == null) {
                return base;
            }

            List<GoodShow> common = mCartData.getCommon();
            List<CartOfActivity> activity = mCartData.getActivity();

            if (common == null && activity == null) {
                return base;
            }

            if (common != null && common.size() > 0) {
                base += common.size();
            }

            if (activity != null && activity.size() > 0) {
                List<GoodShow> goods = activity.get(0).getGoods();
                if (goods != null && goods.size() > 0) {
                    base += goods.size();
                }
            }
            return base;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 2;//猜你喜欢
            } else if (position == getItemCount() - 2) {
                return 1;//空购物车
            }
            return 0;//商品
        }
    }

    private class CartVH extends RecyclerView.ViewHolder {
        /**
         * 活动说明父栏
         */
        private RelativeLayout promotion_floor;
        /**
         * 活动说明栏
         */
        private LinearLayout promotion;
        private LinearLayout combind_ll;
        /**
         * 活动标签
         */
        private TextView tag;
        /**
         * 活动内容
         */
        private TextView name;
        /**
         * 箭头
         */
        private ImageView go;

        private CheckBox good_check;
        private CheckBox check_for_editing;
        private LinearLayout good_content;


        private SpannableTextView total;
        private final SpannableTextView combind_price;


        CartVH(View itemView) {
            super(itemView);
            promotion_floor = itemView.findViewById(R.id.promotion_floor);
            promotion = itemView.findViewById(R.id.promotion);
            tag = itemView.findViewById(R.id.tag);
            name = itemView.findViewById(R.id.name);
            go = itemView.findViewById(R.id.go_promotion);

            good_check = itemView.findViewById(R.id.good_check);
            check_for_editing = itemView.findViewById(R.id.check_for_editing);
            good_content = itemView.findViewById(R.id.good_content);
            combind_ll = itemView.findViewById(R.id.combind_ll);
            combind_price = itemView.findViewById(R.id.combind_price);

            total = itemView.findViewById(R.id.total);
        }

        private void setData(int pos) {
            //1.勾选框展示
            if (inEditing) {
                good_check.setVisibility(View.INVISIBLE);
                check_for_editing.setVisibility(View.VISIBLE);
            } else {
                good_check.setVisibility(View.VISIBLE);
                check_for_editing.setVisibility(View.INVISIBLE);
            }

            //2.数据准备
            List<CartOfActivity> activity = mCartData.getActivity();
            List<GoodShow> common = mCartData.getCommon();

            int actSize = 0;
            if (activity != null && activity.size() > 0 && activity.get(0).getGoods() != null) {
                actSize = activity.get(0).getGoods().size();
            }

            int comSize = 0;
            if (common != null) {
                comSize = common.size();
            }

            //3.活动信息展示：从0开始放置商品,先放活动
            CartOfActivity coa;
            GoodShow good = null;
            if (pos < actSize) {
                //在活动商品范围
                if (activity == null) return;
                coa = activity.get(0);
                good = coa.getGoods().get(pos);

                if (pos == 0) {
                    //第一个则展示活动描述信息
                    promotion_floor.setVisibility(View.VISIBLE);
                    promotion.setVisibility(View.VISIBLE);
                    go.setVisibility(View.GONE);
                    tag.setText(coa.getName());
                    name.setText(coa.getDescription());
                } else {
                    promotion_floor.setVisibility(View.GONE);
                    promotion.setVisibility(View.GONE);
                    go.setVisibility(View.GONE);
                }
            } else if (pos < actSize + comSize) {
                //在无活动范围内
                if (common == null) return;
                good = common.get(pos - actSize);

                if (pos - actSize == 0) {
                    //第一个则展示间距
                    promotion_floor.setVisibility(View.VISIBLE);
                    promotion.setVisibility(View.GONE);
                    go.setVisibility(View.GONE);
                } else {
                    promotion_floor.setVisibility(View.GONE);
                    promotion.setVisibility(View.GONE);
                    go.setVisibility(View.GONE);
                }
            }

            //4.勾选框设置
            final GoodShow finalGood = good;
            if (good != null) {
                if (inEditing) {
                    check_for_editing.setChecked(editChecked.containsKey(finalGood.getId())
                            && editChecked.get(finalGood.getId()).contains(finalGood.getProductId()));
                    check_for_editing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final boolean checked = ((CheckBox) v).isChecked();
                            if (checked) {
                                addEditCheck(finalGood);
                            } else {
                                removeEditCheck(finalGood);
                            }
                        }
                    });
                } else {
                    good_check.setChecked(good.getIsChecked() == Constant.CartChecked.IS_CHECKED);
                    good_check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final boolean checked = ((CheckBox) v).isChecked();
                            ((CheckBox) v).setChecked(!checked);
                            MallApplication.showProgressDialog(true, getActivity());
                            APIConfig.getDataIntoView(new Runnable() {
                                @Override
                                public void run() {
                                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_CHECKITEM)
                                            , new ParamsBuilder()
                                                    .addParam("token", RuntimeConfig.user.getToken())
                                                    .addParam("type", "" + Constant.GoodType.SINGLE)
                                                    .addParam("id", "" + finalGood.getId())
                                                    .addParam("productId", "" + finalGood.getProductId())
                                                    .addParam("isChecked", "" + (checked ? Constant.CartChecked.IS_CHECKED : Constant.CartChecked.NOT_CHECKED))
                                                    .getParams());
                                    final CartResult result = GsonUtil.fromJson(rs, CartResult.class);

                                    UIUtils.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                                mCartData = result.getData();
                                                updateCart();
                                            } else {
                                                ToastUtil.toastByCode(result);
                                            }
                                            MallApplication.showProgressDialog(false, getActivity());
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
                total.setText(PriceFixUtil.format(good.getPrice() * good.getNum()));

                //5.商品设置
                good_content.removeAllViews();
                LinearLayout ll = (LinearLayout) myInflater.inflate(R.layout.item_fragment_cart_good_content, good_content, false);
                ImageView good_img = ll.findViewById(R.id.good_img);
                TextView good_name = ll.findViewById(R.id.good_title);
                TextView good_intro = ll.findViewById(R.id.good_intro);
                LinearLayout unClick = ll.findViewById(R.id.unClick);
                SpannableTextView good_price = ll.findViewById(R.id.good_price);
                final AddMinusButton num = ll.findViewById(R.id.add_minus);

                GlideUtil.GlideInstance(context, good.getImgUrl().get(0)).into(good_img);
                good_name.setText(good.getName());
                good_intro.setText(good.getSpec());
                if (inEditing) {
                    good_intro.setBackgroundColor(UIUtils.getColor(R.color.search_back_grey));
                } else {
                    good_intro.setBackgroundColor(UIUtils.getColor(R.color.white));
                }
                good_intro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inEditing) {
                            PurchaseDialog purchaseDialog = new PurchaseDialog(context, finalGood.getId(), finalGood.getProductId(), new PurchaseDialog.InitGood() {

                                @Override
                                public void changeSpec(final long goodId, final String oldProductId, final String newProductId) {
                                    APIConfig.getDataIntoView(new Runnable() {
                                        @Override
                                        public void run() {
                                            String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_CHANGEITEM)
                                                    , new ParamsBuilder()
                                                            .addParam("token", RuntimeConfig.user.getToken())
                                                            .addParam("type", "" + Constant.GoodType.SINGLE)
                                                            .addParam("id", "" + goodId)
                                                            .addParam("productId", oldProductId)
                                                            .addParam("chnagedProductId", newProductId)
                                                            .getParams());
                                            final CartResult result = GsonUtil.fromJson(rs, CartResult.class);
                                            UIUtils.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                                        mCartData = result.getData();
                                                        updateCart();
                                                    } else {
                                                        ToastUtil.toastByCode(result);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            purchaseDialog.show();
                        }
                    }
                });

                //5.1商品价格、数量、防误触
                good_price.setText(PriceFixUtil.format(good.getPrice()));
                num.initNumber(good.getNum());
                num.setNumberUpdateListener(new AddMinusButton.NumberUpdateListener() {
                    @Override
                    public void numberUpdate(final int number, int changeVal) {
                        MallApplication.showProgressDialog(true, getActivity());
                        APIConfig.getDataIntoView(new Runnable() {
                            @Override
                            public void run() {
                                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_ADJUST)
                                        , new ParamsBuilder()
                                                .addParam("token", RuntimeConfig.user.getToken())
                                                .addParam("type", "" + Constant.GoodType.SINGLE)
                                                .addParam("id", "" + finalGood.getId())
                                                .addParam("productId", "" + finalGood.getProductId())
                                                .addParam("num", "" + number)
                                                .getParams());
                                final CartResult result = GsonUtil.fromJson(rs, CartResult.class);
                                UIUtils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                            mCartData = result.getData();
                                            updateCart();
                                            RuntimeConfig.notifyCartNum();
                                        } else {
                                            ToastUtil.toastByCode(result);
                                        }
                                        MallApplication.showProgressDialog(false, getActivity());
                                    }
                                });
                            }
                        });
                    }
                });
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (inEditing) return;
                        Intent intent = new Intent(context, GoodsDetailActivity.class);
                        intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(finalGood.getId()));
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                    }
                });
//                unClick.setOnClickListener(null);
                good_content.addView(ll);
            }
        }
    }

    private class BlankVH extends RecyclerView.ViewHolder {

        BlankVH(View itemView) {
            super(itemView);
        }

        public void setVisibility(boolean isVisible) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }


    /**
     * 更新购物车
     */
    private void updateCart() {
        adapter.notifyDataSetChanged();
        updateOrderPrice();
    }

    /**
     * 更新结算总价
     */
    private void updateOrderPrice() {
        if (mCartData != null) {
            allTotal.setText(PriceFixUtil.format(mCartData.getTotal()));
        }
    }

    /**
     * 全选或反选
     *
     * @param now 全选或反选
     */
    private void selectAllData(final boolean now) {
        if (!RuntimeConfig.userValid()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
            return;
        }
        if (inEditing) {
            if (now) {
                collectIdMappingProductId(mCartData);
            } else {
                editChecked.clear();
            }
            updateCart();
        } else {
            allSelect.setChecked(!now);
            MallApplication.showProgressDialog(true, getActivity());
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_CHECKITEM_ALL)
                            , new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("isChecked", "" + (now ? Constant.CartChecked.IS_CHECKED : Constant.CartChecked.NOT_CHECKED))
                                    .getParams());
                    final CartResult result = GsonUtil.fromJson(rs, CartResult.class);
                    MallApplication.showProgressDialog(false, getActivity());
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                allSelect.setChecked(now);
                                mCartData = result.getData();
                                updateCart();
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 点击删除
     */
    private void doDelete() {
        if (editChecked != null && editChecked.size() > 0) {
            final String[] typesAndIdsAndProductIds = getIdsFromEditCheck();
            MallApplication.showProgressDialog(true, getActivity());
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_MODIFY)
                            , new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("type", typesAndIdsAndProductIds[0])
                                    .addParam("id", typesAndIdsAndProductIds[1])
                                    .addParam("productId", typesAndIdsAndProductIds[2])
                                    .getParams());
                    CartResult result = GsonUtil.fromJson(rs, CartResult.class);
                    if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                        mCartData = result.getData();
                        editChecked.clear();
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                updateCart();
                                RuntimeConfig.notifyCartNum();
                                doDone();
                            }
                        });
                    } else {
                        ToastUtil.toastByCode(result);
                    }
                    MallApplication.showProgressDialog(false, getActivity());
                }
            });
        } else {
            ToastUtil.toast("请先选择商品！");
        }
    }

    /**
     * 点击编辑
     */
    private void doEdit() {
        if (RuntimeConfig.userValid()) {
            inEditing = true;
            control.setText("完成");
            controlOrder.setVisibility(View.GONE);
            controlDelete.setVisibility(View.VISIBLE);

            updateCart();

        /* 制作一套适配器，记录对应项的id、productId
         * 全部置为空，等待勾选
         */
            editChecked.clear();
            allSelect.setChecked(false);
        }
    }

    /**
     * 点击完成
     */
    private void doDone() {
        inEditing = false;
        control.setText("编辑");
        controlOrder.setVisibility(View.VISIBLE);
        controlDelete.setVisibility(View.GONE);
        updateCart();

        editChecked.clear();
        allSelect.setChecked(false);
    }

    /**
     * 收集所有的id_productId
     *
     * @param cartData
     */
    private void collectIdMappingProductId(CartData cartData) {
        List<GoodShow> com = cartData.getCommon();
        List<CartOfActivity> act = cartData.getActivity();
        if (com != null && com.size() > 0) {
            for (GoodShow gs : com) {
                addEditCheck(gs);
            }
        }

        if (act != null && act.size() > 0) {
            for (CartOfActivity coa : act) {
                List<GoodShow> goods = coa.getGoods();
                if (goods != null && goods.size() > 0) {
                    for (GoodShow gs : goods) {
                        addEditCheck(gs);
                    }
                }
            }
        }
    }

    private void addEditCheck(GoodShow gs) {
        if (editChecked.containsKey(gs.getId())) {
            editChecked.get(gs.getId()).add(gs.getProductId());
        } else {
            HashSet<String> pid = new HashSet<>();
            pid.add(gs.getProductId());
            editChecked.put(gs.getId(), pid);
        }
    }

    private void removeEditCheck(GoodShow gs) {
        if (editChecked.containsKey(gs.getId())) {
            editChecked.get(gs.getId()).remove(gs.getProductId());
            if (editChecked.get(gs.getId()).size() == 0) {
                editChecked.remove(gs.getId());
            }
        }
    }

    private String[] getIdsFromEditCheck() {
        String types = "";
        String ids = "";
        String productIds = "";
        for (Map.Entry<Long, HashSet<String>> entry : editChecked.entrySet()) {
            if (ids.length() != 0) ids += ",";
            ids += String.valueOf(entry.getKey());
            HashSet<String> value = entry.getValue();
            if (value == null || value.size() == 0) continue;
            for (String pid : value) {
                if (productIds.length() != 0) productIds += ",";
                productIds += String.valueOf(pid);
                if (types.length() != 0) types += ",";
                types += Constant.GoodType.SINGLE;
            }
        }
        return new String[]{types, ids, productIds};
    }

    private void addOrder() {

        if (!RuntimeConfig.userValid()) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
            return;
        }
        if (!(mCartData != null && mCartData.getCnt() > 0)) {
            ToastUtil.toast("请先选择商品");
            return;
        }
        MallApplication.showProgressDialog(true, getActivity());
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                final String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_ADD)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .getParams());
                MallApplication.showProgressDialog(false, getActivity());
                BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, OrderMakeActivity.class);
                            intent.putExtra(Constant.Key.KEY_ORDER_UNSURE, rs);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        }
                    });
                } else {
                    ToastUtil.toastByCode(result);
                    if (result != null && result.getMessage().contains("收货地址")) {
                        Intent intent = new Intent(getContext(), OrderAddressEditActivity.class);
                        getActivity().startActivity(intent);
                    }
                }
            }
        });
    }

    public void refreshData() {
        initData();
    }

}
