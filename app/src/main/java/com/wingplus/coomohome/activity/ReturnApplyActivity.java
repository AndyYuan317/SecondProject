package com.wingplus.coomohome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.RemovableImage;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.expend.RoundCornersTransformation;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ImageHelper;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.AfterSaleValid;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.StringArrayResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.wingplus.coomohome.config.DataConfig.CACHE_FILENAME_PREFIX;

/**
 * 提交退货申请
 *
 * @author leaffun.
 *         Create on 2017/10/31.
 */
public class ReturnApplyActivity extends BaseActivity implements View.OnClickListener {

    private TextView mReasonChoose;
    private TextView mReasonRemark;
    private ImageView good_img;
    private TextView good_name;
    private TextView good_intro;
    private SpannableTextView good_price;
    private SpannableTextView nums;

    private String[] reason = new String[]{"其他"};
    private ImageView addBtn;
    private File file;

    // 调用相机拍照时，临时存储文件。
    private Uri imageUri;
    private File imageDir;
    private boolean upLoading;

    private List<String> afterSaleImgs = new ArrayList<>();
    private LinearLayout commentImgContent;
    private EditText name;
    private EditText phone;
    private AfterSaleValid asv;
    private int showNow = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_apply);

        initView();
        initEvent();
        initData();

        imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private void initView() {
        mReasonChoose = findViewById(R.id.return_reason_choose);
        mReasonRemark = findViewById(R.id.return_reason_remark);

        good_img = findViewById(R.id.good_img);
        good_name = findViewById(R.id.good_title);
        good_intro = findViewById(R.id.good_intro);
        good_price = findViewById(R.id.good_price);
        nums = findViewById(R.id.nums);

        addBtn = findViewById(R.id.add_btn);
        commentImgContent = findViewById(R.id.comment_img_content);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
    }

    private void initEvent() {
        mReasonChoose.setOnClickListener(this);
        addBtn.setOnClickListener(this);
    }

    private void initData() {
        asv = (AfterSaleValid) getIntent().getSerializableExtra(Constant.Key.KEY_AFTER_SALE_GOOD_STR);
        GlideUtil.GlideInstance(ReturnApplyActivity.this, asv.getGoodsImg())
                .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(ReturnApplyActivity.this, UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                .into(good_img);
        good_name.setText(asv.getName());
        good_intro.setText(asv.getSpec());
        good_price.setText(PriceFixUtil.format(asv.getPrice()));
        nums.setText(String.valueOf(asv.getNum()));

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_REASON)
                        , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken()).getParams());
                StringArrayResult result = GsonUtil.fromJson(rs, StringArrayResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    reason = result.getData().toArray(new String[result.getData().size()]);
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.push:
                if (upLoading) {
                    ToastUtil.toast("正在上传图片，请稍后");
                    return;
                }
                if ("选择原因".equals(mReasonChoose.getText().toString().trim())) {
                    ToastUtil.toast("请选择原因");
                    return;
                }
                if (mReasonRemark.getText().toString().trim().length() == 0) {
                    ToastUtil.toast("请详细说明退货原因");
                    return;
                }
                if (afterSaleImgs == null || afterSaleImgs.size() <= 0) {
                    ToastUtil.toast("请上传商品照片");
                    return;
                }
                if (name.getText().toString().trim().length() == 0) {
                    ToastUtil.toast("请填写联系人信息");
                    return;
                }
                if (phone.getText().toString().trim().length() == 0) {
                    ToastUtil.toast("请填写联系电话");
                    return;
                }

                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_ADD)
                                , new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("orderId", String.valueOf(asv.getOrderId()))
                                        .addParam("productIds", String.valueOf(asv.getProductId()))
                                        .addParam("reason", mReasonChoose.getText().toString().trim())
                                        .addParam("detail", mReasonRemark.getText().toString().trim())
                                        .addParam("imgs", StringUtil.bindStr(afterSaleImgs, ","))
                                        .addParam("person", name.getText().toString().trim())
                                        .addParam("phone", phone.getText().toString().trim())
                                        .getParams());

                        BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            finish();
                            overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
                break;
            case R.id.return_reason_choose:
                new AlertDialog.Builder(ReturnApplyActivity.this)
                        .setTitle("选择原因")
                        .setSingleChoiceItems(reason, showNow, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mReasonChoose.setText(reason[which]);
                                showNow = which;
                            }
                        }).show();
                break;
            case R.id.add_btn:
                if (PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, ReturnApplyActivity.this)) {
                    pullCamera();
                } else {
                    PermissionUtil.applyCamera(ReturnApplyActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.ImageType.CAMERA) {
                upLoadFile(file);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case PermissionUtil.CAMERA_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    //用户成功授权
                    if (PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, ReturnApplyActivity.this)) {
                        pullCamera();
                    } else {
                        ToastUtil.toast(this, "缺少相机权限");
                    }
                } else {
                    //用户拒绝授权

                    ToastUtil.toast(this, "缺少相机权限");
                    PermissionUtil.goPermissionManager(ReturnApplyActivity.this);
                }
                break;
            default:
                ToastUtil.toast(permsRequestCode + "");
                break;
        }
    }

    private void addImgs(final String url, File file) {
        afterSaleImgs.add(url);
        final RemovableImage removableImage = new RemovableImage(ReturnApplyActivity.this);
        removableImage.setImageFile(file.getAbsolutePath());
        removableImage.setOnRemovableListener(new RemovableImage.OnRemovableListener() {
            @Override
            public void remove(RemovableImage me) {
                afterSaleImgs.remove(url);
                me.setVisibility(View.GONE);
                commentImgContent.removeView(me);
                addBtn.setVisibility(View.VISIBLE);
            }
        });
        commentImgContent.addView(removableImage);
        if (commentImgContent.getChildCount() >= 4) {
            addBtn.setVisibility(View.GONE);
        } else {
            addBtn.setVisibility(View.VISIBLE);
        }
    }

    private void pullCamera() {
        try {
            file = new File(imageDir, CACHE_FILENAME_PREFIX + System.currentTimeMillis() + "temp.jpg");
            imageUri = FileProvider.getUriForFile(ReturnApplyActivity.this, Constant.Provider.getFilePath(), file);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, Constant.ImageType.CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 上传文件
     */
    public void upLoadFile(final File file) {
        upLoading = true;

        ToastUtil.toast("正在上传图片");

        Bitmap bitmap = ImageHelper.CompressImageToSizeOnFilePath(file.getAbsolutePath(), 600, 600);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //补全请求地址
        String requestUrl = APIConfig.getAPIUrl(APIConfig.API_COMMON_UPLOADIMG);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        builder.addFormDataPart("token", RuntimeConfig.user.getToken());
        builder.addFormDataPart("subFolder", "aftersale");
        builder.addFormDataPart("img", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));


        //创建RequestBody
        RequestBody body = builder.build();
        //创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).build();
        final Call call = new OkHttpClient().newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                upLoading = false;
                ToastUtil.toast(MallApplication.getContext(), "上传错误");
                e.printStackTrace();
                try {
                    System.gc();
                    System.runFinalization();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                upLoading = false;
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    StringDataResult baseResult = GsonUtil.fromJson(string, StringDataResult.class);
                    if (baseResult != null && baseResult.getResult() == APIConfig.CODE_SUCCESS) {
                        //保存url
                        final String url = baseResult.getMessage();
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ReturnApplyActivity.this.isDestroyed()) {
                                    return;
                                }
                                addImgs(url, file);

                            }
                        });
                    } else {
                        ToastUtil.toastByCode(baseResult);
                    }
                } else {
                    ToastUtil.toast(MallApplication.getContext(), "上传失败");
                    try {
                        System.gc();
                        System.runFinalization();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }


            }
        });
    }

}
