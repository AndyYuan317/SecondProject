package com.wingplus.coomohome.component;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;


/**
 * 选择图片的模式
 * Created by wangyn on 16/4/27.
 */
public class ImageModeDialog extends Dialog {
    private LinearLayout txtCamera;
    private LinearLayout txtPhoto;
    private TextView txtTitle;

    public ImageModeDialog(Context context){
        super(context, R.style.CustomDialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        initView(context);
    }

    private void initView(Context context){
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_camera_or_photo, null);

        txtCamera = (LinearLayout)mView.findViewById(R.id.txtCamera);
        txtPhoto = (LinearLayout)mView.findViewById(R.id.txtPhoto);
        txtTitle = (TextView)mView.findViewById(R.id.txtTitle);

        setContentView(mView);
    }

    public void setTitle(String title){
        txtTitle.setText(title);
    }

    public void setCameraListener(View.OnClickListener listener){
        txtCamera.setOnClickListener(listener);
    }

    public void setPhotoListener(View.OnClickListener listener){
        txtPhoto.setOnClickListener(listener);
    }
}
