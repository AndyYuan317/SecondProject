package com.wingplus.coomohome.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.util.PermissionUtil;
import com.wingplus.coomohome.util.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 扫一扫页
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class ScanActivity extends BaseActivity implements View.OnClickListener, QRCodeView.Delegate {
    private ZXingView mQRCodeView;
    private ImageView gallery;
    private ImageView light;
    private boolean isLighting;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        gallery = (ImageView) findViewById(R.id.gallery);
        light = (ImageView) findViewById(R.id.light);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, this)) {
            PermissionUtil.applyCamera(this);
        } else {
            startScan();
        }
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case PermissionUtil.CAMERA_REQUEST_CODE:
                boolean cameraAccepted = (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (cameraAccepted) {
                    if (PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, ScanActivity.this)) {
                        startScan();
                    } else {
                        ToastUtil.toast(this, "缺少相机权限");
                        finish();
                    }
                } else {
                    //用户授权拒绝
                    ToastUtil.toast(this, "缺少相机权限");
                    PermissionUtil.goPermissionManager(ScanActivity.this);
                    finish();
                }
                break;
            default:
                ToastUtil.toast(permsRequestCode + "");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.gallery:
                //处理图库
//                handleGallery();
                break;
            case R.id.light:
                //处理灯光
                handleLight();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (data.getData() != null) {
                Uri picPath = data.getData();
//                    LogUtil.i("picpath",picPath.getPath());
//                ToastUtil.toast(ScanActivity.this, picPath.getPath());
                new QRCodeTask(picPath).execute();
            }
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


    /**
     * 开始扫描
     */
    private void startScan() {
        mQRCodeView.startCamera();
        //mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
        mQRCodeView.startSpotDelay(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogUtil.i("ScanActivity", "result:" + result);
        handleString(result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        LogUtil.i("ScanActivity", "result:打开相机错误");
    }

    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    /**
     * 解码图片二维码
     */
    class QRCodeTask extends AsyncTask<Uri, Void, String> {

        private Uri path;

        public QRCodeTask(Uri uri) {
            path = uri;
        }

        @Override
        protected String doInBackground(Uri... strings) {

            LogUtil.i("扫码", "后台图片处理");
            try {
                String s = QRCodeDecoder.syncDecodeQRCode(CompressImage(decodeUriAsBitmap(path)));
                LogUtil.e("扫码", "结果" + s);
                return s;
            } catch (Exception e) {
                LogUtil.e("扫码", "结果---------");
                e.printStackTrace();
            } finally {
                bitmap = null;
            }
            return "未扫码到二维码";

        }

        @Override
        protected void onPostExecute(String s) {
            LogUtil.e("扫码", "更新图片");
//            gallery.setImageBitmap(bitmap);
            handleString(s);
        }
    }

    /**
     * 处理扫码结果
     *
     * @param url
     */
    private void handleString(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            Intent web = new Intent(ScanActivity.this, WebActivity.class);
            web.putExtra(Constant.Key.KEY_WEB_URL, url);
            startActivity(web);
            finish();
            return;
        }

        vibrate();
        mQRCodeView.startSpot();


        //网页链接的处理
        try {
            //自定义的scheme
            if (url.startsWith(getResources().getString(R.string.app_url_scheme)) || url.startsWith("alipays://") ||
                    url.startsWith("mailto://") || url.startsWith("tel://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                finish();
            } else {
                // 或者展示信息提供复制
                mQRCodeView.stopCamera();
                finish();
            }
        } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)

        }

    }

    /**
     * 处理灯光
     */
    private void handleLight() {
        try {
            if (isLight()) {
                mQRCodeView.openFlashlight();
                isLighting = true;
            } else {
                mQRCodeView.closeFlashlight();
                isLighting = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("闪光灯调用问题", e.getMessage());
        }
    }

    /**
     * 打开图库
     */
    private void handleGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
//        intent.putExtra("crop", "false");
//        intent.putExtra("outputX", mHeadImageSize);
//        intent.putExtra("outputY", mHeadImageSize);
//        intent.putExtra("scale", false);

        // 如果文件大小小于设定的mHeadImageSize,则直接从data中返回
        intent.putExtra("return-data", true);
        // 如果文件大小大于设定的mHeadImageSize,则通过imageUri返回
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Constant.ImageType.PHOTO);
    }

    /**
     * 灯光状态
     *
     * @return
     */
    private boolean isLight() {
        return isLighting;
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
     * 缩小图片到尺寸以内，并尽量降低图片大小小于100K
     */
    public static Bitmap CompressImageToSize(Bitmap image, int height, int width) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        int w = image.getWidth();
        int h = image.getHeight();
        int be = 1;
        if (w > h && w > width) {
            be = w / width;
        } else if (w < h && h > height) {
            be = h / height;
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return CompressImage(bitmap);
    }

    /**
     * 降低图片质量来压缩图片，尽量小于100K
     */
    public static Bitmap CompressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100 && options >= 10) {
            options -= 10;
            baos.reset();
            // 这种方法保存了图片所有的像素，不能无限压缩图片大小
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }
}
