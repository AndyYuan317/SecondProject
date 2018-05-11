package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.TestDataProduceUtil;
import com.wingplus.coomohome.util.TimeUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.XiaoNengUtil;
import com.wingplus.coomohome.web.Dialog;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.CursrvResult;

import java.util.List;

import cn.xiaoneng.coreapi.ChatParamsBody;
import cn.xiaoneng.uiapi.Ntalker;
import cn.xiaoneng.utils.CoreData;

/**
 * 在线客服
 *
 * @author leaffun.
 *         Create on 2017/10/14.
 */
public class CustomerServiceActivity extends BaseActivity implements View.OnClickListener {

    private List<Dialog> dl;
    private RecyclerView rv;
    private DialogAdapter adapter;
    private EditText input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        startXiaoNengChat();

//        getQQ();

//        initView();
//        initData();
    }

    /**
     * 转接小能客服
     */
    private void startXiaoNengChat() {

        XiaoNengUtil.requestPermissions(CustomerServiceActivity.this);

        XiaoNengUtil.setHeadImgCircle(true);

        ChatParamsBody chatparams = new ChatParamsBody();

        // 咨询发起页（专有参数）
//        chatparams.startPageTitle = "1111111女装/女士精品 - 服饰 - 搜索店铺 - ECMall演示站";
//        chatparams.startPageUrl = "http://img.meicicdn.com/p/51/050010/h-050010-1.jpg";


        //			// 此参数不传就默认在sdk外部打开，即在onClickUrlorEmailorNumber方法中打开
        //			chatparams.clickurltoshow_type = CoreData.CLICK_TO_APP_COMPONENT;
        //
        //			// 商品展示（专有参数）
//        chatparams.itemparams.clientgoodsinfo_type = CoreData.SHOW_GOODS_BY_ID;
//        chatparams.itemparams.appgoodsinfo_type = CoreData.SHOW_GOODS_BY_ID;
//        chatparams.itemparams.goods_id = "ntalker_test";//ntalker_test

        try {
            //从商品详情进入则展示商品信息
            Intent intent = getIntent();
            String goodsName = intent.getStringExtra("goodsName");
            String goodsPrice = intent.getStringExtra("goodsPrice");
            String goodsImage = intent.getStringExtra("goodsImage");
            String goodsId = intent.getStringExtra("goodsId");

            if(goodsName != null && goodsName.length() > 0) {
                chatparams.itemparams.appgoodsinfo_type = CoreData.SHOW_GOODS_BY_WIDGET;
                chatparams.itemparams.goods_name = goodsName;
                chatparams.itemparams.goods_price = goodsPrice;
                chatparams.itemparams.goods_image = goodsImage; //URL必须以"http://"开头
                chatparams.itemparams.goods_url = "";    //URL必须以"http://"开头
                chatparams.itemparams.clicktoshow_type = CoreData.CLICK_TO_APP_COMPONENT; //禁止商品点击
                chatparams.itemparams.clientgoodsinfo_type = CoreData.SHOW_GOODS_BY_ID;
                chatparams.itemparams.goods_id = goodsId;//示例：ntalker_test，传入商品id
                chatparams.itemparams.itemparam = "";//示例：App，商品扩展参数（选填），所传参数由商品接口确定
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        chatparams.kfuid = XiaoNengUtil.SITEID+"_ISME9754_T2D_admin";

        int startChatResult = Ntalker.getBaseInstance().startChat(this, XiaoNengUtil.SETTINGID1, XiaoNengUtil.GROUP_NAME, chatparams, XiaoNengChatActivity.class);

        if (0 == startChatResult) {
            LogUtil.e("startChat", "打开聊窗成功");
        } else {
            LogUtil.e("startChat", "打开聊窗失败，错误码:" + startChatResult);
        }

        finish();

        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
    }

    private void getQQ() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COMMON_CURSRV),
                        new ParamsBuilder().getParams());
                CursrvResult result = GsonUtil.fromJson(rs, CursrvResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    try {
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + result.getData().getQq();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ToastUtil.toast("请先下载并安装QQ");
                    } finally {
                        finish();
                    }
                } else {
                    ToastUtil.toastByCode(result);
                    finish();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.send:
                String s = input.getText().toString().trim();
                if (s.length() == 0) {
                    return;
                } else {
                    input.setText("");
                    doSend(s);
                }
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
        rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

        ((DefaultItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);

        input = findViewById(R.id.input);
    }

    private void initData() {
        dl = TestDataProduceUtil.getDialog();

        if (dl != null && dl.size() > 0) {
            adapter = new DialogAdapter();
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            rv.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private class DialogAdapter extends RecyclerView.Adapter<DialogVH> {

        @Override
        public DialogVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DialogVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_customer_service, parent, false));
        }

        @Override
        public void onBindViewHolder(DialogVH holder, int position) {
            holder.setData(dl.get(position));
        }

        @Override
        public int getItemCount() {
            return dl.size();
        }
    }

    class DialogVH extends RecyclerView.ViewHolder {

        private final LinearLayout contentCustomer;
        private final LinearLayout contentUser;
        private final TextView newsUser;
        private final TextView newsCustomer;
        private final TextView dialogTime;

        DialogVH(View itemView) {
            super(itemView);

            contentCustomer = itemView.findViewById(R.id.content_customer);
            contentUser = itemView.findViewById(R.id.content_user);
            newsUser = itemView.findViewById(R.id.user_news);
            newsCustomer = itemView.findViewById(R.id.customer_news);
            dialogTime = itemView.findViewById(R.id.dialog_time);
        }

        public void setData(Dialog dialog) {
            if (dialog.getStaffNews() == null || dialog.getStaffNews().length() == 0) {
                contentCustomer.setVisibility(View.GONE);
                contentUser.setVisibility(View.VISIBLE);
                newsUser.setText(dialog.getUserNews());
            } else {
                contentCustomer.setVisibility(View.VISIBLE);
                contentUser.setVisibility(View.GONE);
                newsCustomer.setText(dialog.getStaffNews());
            }

            LogUtil.i("dialogTime", dialog.getSendTime() + "");
            if (dialog.getSendTime() > 0) {
                dialogTime.setVisibility(View.VISIBLE);
                dialogTime.setText(TimeUtil.mesToDate(dialog.getSendTime()));
            } else {
                dialogTime.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 发送消息给客服
     */
    private void doSend(String s) {
        //TODO 时间传递给后台 然后重新获取数据再决定是否显示时间


        Dialog dialog = new Dialog();
//        dialog.setSendTime(System.currentTimeMillis());
        dialog.setUserNews(s);
        dl.add(dialog);

        adapter.notifyDataSetChanged();
        rv.scrollToPosition(adapter.getItemCount() - 1);
    }
}
