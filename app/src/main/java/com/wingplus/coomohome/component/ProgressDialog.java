package com.wingplus.coomohome.component;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.RuntimeConfig;

/**
 * @author leaffun.
 *         Create on 2017/11/30.
 */
public class ProgressDialog extends Dialog {

    private Context context;

    public ProgressDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;

        initView();
    }

    private void initView() {
        View mView = LayoutInflater.from(context).inflate(R.layout.component_progress, null);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        setContentView(mView);
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
