package com.wingplus.coomohome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.OrderUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.OrderAddressResult;

import java.util.List;

/**
 * 收货地址
 *
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class OrderAddressActivity extends BaseActivity implements View.OnClickListener {


    private RecyclerView rv;
    private List<OrderAddress> oas;
    private OAAdapter adapter;
    private ImageView empty;
    private boolean canChose = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_address);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.add_address:
                Intent intent = new Intent(getApplicationContext(), OrderAddressEditActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        String from = getIntent().getStringExtra("from");
        if("OrderMakeActivity".equals(from)){
            canChose = true;
        }

        rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new OAAdapter();
        rv.setAdapter(adapter);

        empty = findViewById(R.id.empty);
        LinearLayout addAddress = findViewById(R.id.add_address);
        addAddress.setOnClickListener(this);
    }

    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ADDRESS_LIST)
                        , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                .getParams());

                final OrderAddressResult result = GsonUtil.fromJson(rs, OrderAddressResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            oas = result.getData();
                            OrderUtil.orderAddress(oas);
                            if (oas != null && oas.size() > 0) {
                                empty.setVisibility(View.GONE);
                                rv.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            } else {
                                // 显示空界面
                                empty.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            ToastUtil.toastByCode(result);
                            // 显示空界面
                            empty.setVisibility(View.VISIBLE);
                            rv.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    private class OAAdapter extends RecyclerView.Adapter<OAVH> {

        @Override
        public OAVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OAVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_order_address, parent, false));
        }

        @Override
        public void onBindViewHolder(OAVH holder, int position) {
            holder.setData(oas.get(position));
        }

        @Override
        public int getItemCount() {
            return oas == null ? 0 : oas.size();
        }
    }

    /**
     * 收货地址 VH
     */
    class OAVH extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView phone;
        private final TextView addressDefault;
        private final TextView addres;
        private final ImageView delete;
        private final ImageView edit;
        private final LinearLayout back;

        OAVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.acceptor_name);
            phone = itemView.findViewById(R.id.acceptor_phone);
            addressDefault = itemView.findViewById(R.id.acceptor_address_default);
            addres = itemView.findViewById(R.id.acceptor_address);
            delete = itemView.findViewById(R.id.delete_address);
            edit = itemView.findViewById(R.id.edit);
            back = itemView.findViewById(R.id.back);
        }

        public void setData(final OrderAddress address) {
            name.setText(address.getPerson());
            phone.setText(address.getPhone());
            addres.setText(address.getProvince() + address.getCity() + address.getDistrict() + address.getAddress());
            addressDefault.setVisibility(address.getIsDefault() == Constant.OrderAddress.IS_DEFAULT ? View.VISIBLE : View.INVISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), OrderAddressEditActivity.class);
                    intent.putExtra(Constant.Key.KEY_ORDER_ADDRESS, address);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(OrderAddressActivity.this, R.style.AlertSelf).setTitle("确认删除地址吗？")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确认”后的操作
                                    APIConfig.getDataIntoView(new Runnable() {
                                        @Override
                                        public void run() {
                                            MallApplication.showProgressDialog(true,OrderAddressActivity.this);
                                            String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ADDRESS_DELETE),
                                                    new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                                            .addParam("addressId", "" + address.getId())
                                                            .getParams());
                                            BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                                UIUtils.runOnUIThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        initData();
                                                    }
                                                });
                                            }
                                            ToastUtil.toastByCode(result);
                                            MallApplication.showProgressDialog(false, OrderAddressActivity.this);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("不了", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“返回”后的操作,这里不设置没有任何操作
                                }
                            }).show();
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(canChose) {
                        Intent intent = new Intent();
                        intent.putExtra(Constant.Key.KEY_ORDER_ADDRESS, address);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }


    }
}
