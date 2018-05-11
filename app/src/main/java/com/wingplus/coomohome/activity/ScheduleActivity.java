package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.TimeLineLinearLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.LogisticsEntity;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.LogisticsResult;

import java.util.List;

/**
 * 物流进度
 *
 * @author leaffun.
 *         Create on 2017/10/25.
 */
public class ScheduleActivity extends BaseActivity implements View.OnClickListener {

    private TimeLineLinearLayout timeline;
    private TextView lcn;

    private ImageView imgFetch;
    private ImageView imgOnway;
    private ImageView imgPush;
    private ImageView imgSign;

    private TextView txtFetch;
    private TextView txtOnway;
    private TextView txtPush;
    private TextView txtSign;

    private TextView lineFetch;
    private TextView lineOnway;
    private TextView linePush;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);
        initView();
        initData();
    }

    private void initView() {
        timeline = findViewById(R.id.timeline);
        lcn = findViewById(R.id.logistics_com_no);

        imgFetch = findViewById(R.id.img_fetch);
        imgOnway = findViewById(R.id.img_onway);
        imgPush = findViewById(R.id.img_push);
        imgSign = findViewById(R.id.img_sign);

        txtFetch = findViewById(R.id.txt_fetch);
        txtOnway = findViewById(R.id.txt_onway);
        txtPush = findViewById(R.id.txt_push);
        txtSign = findViewById(R.id.txt_sign);

        lineFetch = findViewById(R.id.line_fetch);
        lineOnway = findViewById(R.id.line_onway);
        linePush = findViewById(R.id.line_push);
    }

    private void initData() {
        final String orderId = getIntent().getStringExtra(Constant.Key.KEY_ORDER_ID_OR_CODE);
        if (orderId != null && orderId.length() > 0) {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_LOGISTICS)
                            , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("orderId", orderId)
                                    .getParams());
                    final LogisticsResult result = GsonUtil.fromJson(rs, LogisticsResult.class);
                    if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                List<LogisticsEntity> data = result.getData().getData();
                                if (data != null && data.size() > 0) {
                                    for (int i = 0; i < data.size(); i++) {
                                        addItem(data.get(i), i != 0);
                                    }
                                }
                                lockNow(result.getData().getState());
                                lcn.setText(result.getData().getLogiName() + " " + result.getData().getNu());
                            }
                        });
                    } else {
                        ToastUtil.toastByCode(result);
                        finish();
                    }
                }
            });
        } else {
            finish();
        }
    }

    /**
     * 展示现在的进度图标
     */
    private void lockNow(String state) {
        int transtate = Constant.LogisticState.transform(state);

        boolean fetch = transtate >= Constant.LogisticState.INT_FETCH;
        boolean onway = transtate >= Constant.LogisticState.INT_ONWAY;
        boolean push = transtate >= Constant.LogisticState.INT_PUSH;
        boolean sign = transtate >= Constant.LogisticState.INT_SIGN;

        imgFetch.setImageDrawable(UIUtils.getDrawable(fetch ? R.drawable.icon_recipient : R.drawable.icon_recipient_no));
        imgOnway.setImageDrawable(UIUtils.getDrawable(onway ? R.drawable.icon_transport : R.drawable.icon_transport_no));
        imgPush.setImageDrawable(UIUtils.getDrawable(push ? R.drawable.icon_delivery : R.drawable.icon_delivery_no));
        imgSign.setImageDrawable(UIUtils.getDrawable(sign ? R.drawable.icon_sign : R.drawable.icon_sign_no));

        int fetchColor = UIUtils.getColor(fetch ? R.color.titleBlack : R.color.titleGray);
        int onwayColor = UIUtils.getColor(onway ? R.color.titleBlack : R.color.titleGray);
        int pushColor = UIUtils.getColor(push ? R.color.titleBlack : R.color.titleGray);
        int signColor = UIUtils.getColor(sign ? R.color.titleBlack : R.color.titleGray);

        txtFetch.setTextColor(fetchColor);
        txtOnway.setTextColor(onwayColor);
        txtPush.setTextColor(pushColor);
        txtSign.setTextColor(signColor);
        lineFetch.setBackgroundColor(fetchColor);
        lineOnway.setBackgroundColor(onwayColor);
        linePush.setBackgroundColor(pushColor);
    }

    private void addItem(LogisticsEntity li, boolean divider) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_timeline, timeline, false);
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(li.getTime());
        ((TextView) v.findViewById(R.id.tx_action_person)).setText(li.getContext());
        v.findViewById(R.id.tx_action_divider).setVisibility(divider ? View.VISIBLE : View.GONE);
        timeline.addView(v);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
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
}
