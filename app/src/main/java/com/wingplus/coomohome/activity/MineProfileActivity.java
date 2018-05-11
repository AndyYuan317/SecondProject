package com.wingplus.coomohome.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.ImageModeDialog;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.AndroidFileUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ImageHelper;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.TimeUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.WeixinUtil;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.LoginResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 我的资料页
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class MineProfileActivity extends BaseActivity implements View.OnClickListener {

    private TextView sex;
    private TextView birthday;
    private TextView save;
    private DatePickerDialog datePickerDialog;
    private EditText name;

    private boolean inEditing = false;
    private ImageView headImg;

    // 截取图片的大小
    private int mHeadImageSize = 800;
    // 调用相机拍照时，临时存储文件。
    private Uri imageUri;
    private File imageDir;
    private String headImageUrl = "";
    private ImageModeDialog dialog;
    private TextView phone;
    private TextView phone_bind;
    private TextView wx_bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_profile);

        initView();
        checkBind();

        imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageUri = Uri.fromFile(new File(imageDir, "headimage.jpg"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBind();
    }

    private void initView() {
        save = findViewById(R.id.save);

        headImg = findViewById(R.id.img);
        TextView id = findViewById(R.id.id);
        TextView account = findViewById(R.id.account);
        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);
        birthday = findViewById(R.id.birthday);
        phone = findViewById(R.id.phone);
        phone_bind = findViewById(R.id.phone_bind);
        wx_bind = findViewById(R.id.wx_bind);

        id.setText(RuntimeConfig.user.getId());
        account.setText(RuntimeConfig.user.getUserCode());
        name.setText(RuntimeConfig.user.getUserName());
        sex.setText(Constant.SexType.getStr(RuntimeConfig.user.getSex()));
        birthday.setText(RuntimeConfig.user.getBirthday());
        if (RuntimeConfig.user.getHeadImgUrl() != null && RuntimeConfig.user.getHeadImgUrl().length() > 0) {
            GlideUtil.GlideInstance(MineProfileActivity.this, RuntimeConfig.user.getHeadImgUrl()).into(headImg);
        }
    }

    private void initBind() {
        if (!RuntimeConfig.userValid())
            return;
        String mobile = RuntimeConfig.user.getMobile();
        if (mobile != null && !mobile.isEmpty()) {
            phone.setText(mobile);
            phone_bind.setText("已绑定");
            phone_bind.setOnClickListener(null);
        } else {
            phone_bind.setText("去绑定");
            phone_bind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MineProfileActivity.this, BindActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.dialog_enter,R.anim.dialog_exit);
                }
            });
        }
        String weixinId = RuntimeConfig.user.getWeixinId();
        if (weixinId != null && !weixinId.isEmpty()) {
            wx_bind.setText("已绑定");
            wx_bind.setOnClickListener(null);
        } else {
            wx_bind.setText("去绑定");
            wx_bind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeixinUtil.wxBind();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.img:
                Intent intent = new Intent(MineProfileActivity.this, ZoomImageActivity.class);
                intent.putExtra("resource", RuntimeConfig.user.getHeadImgUrl());
                startActivity(intent);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.name:
                if (inEditing)
                    name.setCursorVisible(true);
                break;
            case R.id.sex:
                if (inEditing)
                    showSexPickDlg();
                break;
            case R.id.birthday:
                if (inEditing)
                    showDatePickDlg();
                break;
            case R.id.save:
                if (inEditing) {
                    doSaveProfile();
                } else {
                    name.setFocusableInTouchMode(true);
                    name.setFocusable(true);
                    name.setCursorVisible(false);
                    save.setText("保存");
                    inEditing = true;
                }
                break;
            case R.id.modify_headImg:
                showImageModeDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSexPickDlg() {
        new AlertDialog.Builder(this)
                .setTitle("性别")
                .setSingleChoiceItems(new String[]{Constant.SexType.SECRET_STR, Constant.SexType.MALE_STR, Constant.SexType.FEMALE_STR},
                        Constant.SexType.getInt(sex.getText().toString()), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                sex.setText(Constant.SexType.getStr(which));
                            }
                        }).show();
    }

    protected void showDatePickDlg() {
        String bs = birthday.getText().toString();
        if (bs.length() == 0) {
            bs = TimeUtil.mesToYMD(System.currentTimeMillis());
        }
        String[] strings = bs.split("-");
        datePickerDialog = new DatePickerDialog(MineProfileActivity.this, R.style.MyDatePickerStyle, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                datePickerDialog.dismiss();
            }
        }, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
        datePickerDialog.show();
    }

    /**
     * 保存修改的信息
     */
    private void doSaveProfile() {
        final String userName = name.getText().toString().trim();
        if (userName.length() < 0) {
            ToastUtil.toast("请填写您的昵称");
            return;
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {

                String resultString = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_USER_MODIFY),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("userName", userName)
                                .addParam("sex", "" + Constant.SexType.getInt(sex.getText().toString().trim()))
                                .addParam("birthday", birthday.getText().toString().trim())
                                .addParam("headImgUrl", headImageUrl.length() > 0 ? headImageUrl : RuntimeConfig.user.getHeadImgUrl())
                                .getParams());
                LoginResult loginResult = GsonUtil.fromJson(resultString, LoginResult.class);
                if (loginResult != null && loginResult.getResult() == APIConfig.CODE_SUCCESS) {
                    RuntimeConfig.user = loginResult.getData();
                    ToastUtil.toast(MallApplication.getContext(), "保存成功");
                    finish();
                } else {
                    ToastUtil.toastByCode(loginResult);
                }
            }
        });
    }


    private void showImageModeDialog() {
        dialog = new ImageModeDialog(this);
        dialog.setCameraListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, MineProfileActivity.this)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, Constant.ImageType.CAMERA);
                } else {
                    PermissionUtil.applyCamera(MineProfileActivity.this);
                }
            }
        });
        dialog.setPhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setType("image/*");
                startActivityForResult(intent, Constant.ImageType.PHOTO);
            }
        });
        dialog.show();
    }

    /**
     * 接受调用相机和画册后，回调的数据。
     *
     * @param requestCode 请求的任务编号  HeadImgType.CAMERA ／ HeadImgType.PHOTO
     * @param resultCode  结果编号       是否调用成功
     * @param data        回传数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            switch (requestCode) {
                case Constant.ImageType.CAMERA: {
                    sendImageToCrop(imageUri);
                    break;
                }
                case Constant.ImageType.PHOTO: {
                    // 由于从系统图库中取图片时，如果图片大小小于设定值，则从data中直接返回。
                    // 否则使用外部输出文件来返回，因为不能判断用户选择的文件大小。
                    // 因此首先从data中取数据，如果取不到则从外部存储文件中取，用来保证获得用户选择的内容。

                    String path = null;
                    if (data.getData() != null) {
                        path = AndroidFileUtil.getPath(MineProfileActivity.this, data.getData());
                    }
                    if (path == null) {
                        path = imageUri.getPath();
                    }
                    if (path != null) {
                        sendImageToCrop(Uri.fromFile(new File(path)));
//                        bitmap = ImageHelper.CompressImageToSizeOnFilePath(path, mHeadImageSize, mHeadImageSize);
//                        saveImage(bitmap);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        final byte[] bytes = baos.toByteArray();
//                        UIUtils.runOnUIThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                GlideUtil.GlideInstance(MineProfileActivity.this, bytes).into(headImg);
//                            }
//                        });

                    }
                    break;
                }
                case Constant.ImageType.CROP: {
                    bitmap = decodeUriAsBitmap(imageUri);
                    if (bitmap != null) {
                        bitmap = ImageHelper.CompressImageToSizeOnFilePath(imageUri.getPath(), mHeadImageSize, mHeadImageSize);
                        saveImage(bitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        final byte[] bytes = baos.toByteArray();
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                GlideUtil.GlideInstance(MineProfileActivity.this, bytes).into(headImg);
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    /**
     * 将URI的内容转换为Bitmap
     *
     * @param uri 资源定位器
     * @return Bitmap
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (Exception e) {
            LogUtil.e("decodeUriAsBitmap", e.getMessage());
        }
        return bitmap;
    }

    /**
     * 图片裁剪
     *
     * @param uri 带裁剪的图片Uri
     */
    private void sendImageToCrop(Uri uri) {
        if (uri == null) return;

        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputX", mHeadImageSize);
        intent.putExtra("outputY", mHeadImageSize);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, Constant.ImageType.CROP);
    }

    public void saveImage(Bitmap bmp) {
        File appDir = imageDir;
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "headimage.jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //上传图片
        upLoadImage(file);
    }

    public void upLoadImage(File file) {
        upLoadFile(file);
    }

    /**
     * 上传文件
     */
    public void upLoadFile(File file) {
        //补全请求地址
        String requestUrl = APIConfig.getAPIUrl(APIConfig.API_COMMON_UPLOADIMG);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        builder.addFormDataPart("token", RuntimeConfig.user.getToken());
        builder.addFormDataPart("subFolder", "face");
        builder.addFormDataPart("img", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));


        //创建RequestBody
        RequestBody body = builder.build();
        //创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).build();
        final Call call = new OkHttpClient().newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ToastUtil.toast(MallApplication.getContext(), "上传错误");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    StringDataResult baseResult = GsonUtil.fromJson(string, StringDataResult.class);
                    if (baseResult != null && baseResult.getResult() == APIConfig.CODE_SUCCESS) {
                        ToastUtil.toast(getApplicationContext(), "上传图片成功，正在保存...");
                        //保存url
                        headImageUrl = baseResult.getMessage();
                        doSaveHeadImg();
                    } else {
                        ToastUtil.toastByCode(baseResult);
                    }
                } else {
                    ToastUtil.toast(MallApplication.getContext(), "上传失败");
                }
            }
        });
    }

    private void doSaveHeadImg() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {

                String resultString = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_USER_HEADIMG_CHANGE),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("headImgUrl", headImageUrl.length() > 0 ? headImageUrl : RuntimeConfig.user.getHeadImgUrl())
                                .getParams());
                LoginResult loginResult = GsonUtil.fromJson(resultString, LoginResult.class);
                if (loginResult != null && loginResult.getResult() == APIConfig.CODE_SUCCESS) {
                    RuntimeConfig.user = loginResult.getData();
                    ToastUtil.toast(MallApplication.getContext(), "保存成功");
                    finish();
                } else {
                    ToastUtil.toastByCode(loginResult);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case PermissionUtil.CAMERA_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    //用户成功授权
                    if (PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, MineProfileActivity.this)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, Constant.ImageType.CAMERA);
                    } else {
                        ToastUtil.toast(this, "缺少相机权限");
                    }
                } else {
                    //用户拒绝授权

                    ToastUtil.toast(this, "缺少相机权限");
                    PermissionUtil.goPermissionManager(MineProfileActivity.this);
                }
                break;
            default:
                ToastUtil.toast(permsRequestCode + "");
                break;
        }
    }

    /**
     * 检查是否需要绑定。
     */
    private void checkBind(){

        if (!RuntimeConfig.userValid())
            return;
        String mobile = RuntimeConfig.user.getMobile();
        String weixinId = RuntimeConfig.user.getWeixinId();

        boolean needBind = getIntent().getBooleanExtra("bind", false);
        if(!needBind){
            return;
        }
        if (mobile == null || mobile.isEmpty()) {
            Intent intent = new Intent(MineProfileActivity.this, BindActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.dialog_enter,R.anim.dialog_exit);
        }else if(weixinId == null || weixinId.isEmpty()) {
            WeixinUtil.wxBind();
        }
    }
}
