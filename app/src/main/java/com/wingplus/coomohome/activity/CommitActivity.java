package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.MyRatingStar;
import com.wingplus.coomohome.component.RemovableImage;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
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
import com.wingplus.coomohome.web.CommitEntity;
import com.wingplus.coomohome.web.entity.CommitJson;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.OrderMake;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.OrderMakeResult;
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
 * 发表评价
 *
 * @author leaffun.
 *         Create on 2017/10/10.
 */
public class CommitActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rv;
    private CommitAdapter adapter;

    private List<CommitEntity> commits = new ArrayList<>();
    private CommitJson commitJson = new CommitJson();


    // 调用相机拍照时，临时存储文件。
    private Uri imageUri;
    private File imageDir;
    private File file;
    private boolean upLoading;
    private int p_upLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit);

        initView();
        initData();

        imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private void initView() {
        rv = findViewById(R.id.rv);

        LinearLayoutManager manager = new LinearLayoutManager(CommitActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new CommitAdapter();
        rv.setAdapter(adapter);
    }

    private void initData() {
        final String orderId = getIntent().getStringExtra(Constant.Key.KEY_ORDER_ID_OR_CODE);
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_INFO)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("id", orderId)
                                .getParams());
                final OrderMakeResult result = GsonUtil.fromJson(rs, OrderMakeResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    OrderMake mOrder = result.getData();
                    if (mOrder == null) {
                        ToastUtil.toast("请刷新订单");
                        finish();
                    } else {
                        List<GoodShow> goods = mOrder.getSubOrders().get(0).getGoods();
                        for (GoodShow gs : goods) {
                            CommitEntity entity = new CommitEntity();
                            entity.setGoodsId(String.valueOf(gs.getId()));
                            entity.setProductId(gs.getProductId());
                            if (gs.getImgUrl() != null) {
                                entity.setGoodImg(gs.getImgUrl().get(0));
                            }
                            commits.add(entity);
                        }
                        commitJson.setOrderId(mOrder.getSubOrders().get(0).getId());
                        commitJson.setName(RuntimeConfig.user.getUserName());
                        commitJson.setHeadImg(RuntimeConfig.user.getHeadImgUrl());
                        commitJson.setLevel(RuntimeConfig.user.getLvName());
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                } else {
                    ToastUtil.toastByCode(result);
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.commit:
                doCommit();
                break;
            default:
                break;
        }
    }

    private class CommitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int index;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new CommitVH(LayoutInflater.from(CommitActivity.this).inflate(R.layout.vh_activity_commit, parent, false));
            } else {
                return new OrderInfoVH(LayoutInflater.from(CommitActivity.this).inflate(R.layout.vh_activity_commit_order_info, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CommitVH) {
                CommitVH holder1 = (CommitVH) holder;
                final int i = position;
                holder1.setData(position);

                //触摸时间监听
                holder1.goodComment.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            //记录当前点击的et
                            index = i;
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            //触摸时拦截touch事件
                            if (canVerticalScroll((EditText) v)) {
                                //按下时如果文字可以滚动
                                v.getParent().requestDisallowInterceptTouchEvent(true);//阻止父控件拦截touch事件
                            }

                        }
                        return false;
                    }
                });

                holder1.goodComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);//让父控件能够拦截ontouch事件
                    }
                });

//
                holder1.goodComment.clearFocus();
                holder1.goodComment.setSelection(holder1.goodComment.getText().length());

                if (index != -1 && index == i) {
                    //强制加上焦点
                    holder1.goodComment.requestFocus();
                    //设置光标显示到编辑框尾部
                    holder1.goodComment.setSelection(holder1.goodComment.getText().length());
                    //重置
                    index = -1;
                }

            } else if (holder instanceof OrderInfoVH) {

            }
        }

        @Override
        public int getItemCount() {
            return (commits == null ? 0 : commits.size()) + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() - 1 > 0 && position < getItemCount() - 1) {
                return 1;
            }
            return 0;
        }

        private boolean canVerticalScroll(EditText editText) {
            //滚动的距离
            int scrollY = editText.getScrollY();
            //控件内容的总高度
            int scrollRange = editText.getLayout().getHeight();
            //控件实际显示的高度
            int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
            //控件内容总高度与实际显示高度的差值
            int scrollDifference = scrollRange - scrollExtent;

            if (scrollDifference == 0) {
                return false;
            }

            return (scrollY > 0) || (scrollY < scrollDifference - 1);
        }
    }

    private class CommitVH extends RecyclerView.ViewHolder {

        private final ImageView goodImg;
        private final MyRatingStar goodTotalStar;
        private final EditText goodComment;
        private final LinearLayout commentImgContent;
        private final ImageView addBtn;

        private int p;

        CommitVH(View itemView) {
            super(itemView);
            goodImg = itemView.findViewById(R.id.good_img);
            goodTotalStar = itemView.findViewById(R.id.good_total_star);
            goodComment = itemView.findViewById(R.id.good_comment);
            commentImgContent = itemView.findViewById(R.id.comment_img_content);
            addBtn = itemView.findViewById(R.id.add_btn);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.good_total_star:
                            commits.get(p).setScore(((MyRatingStar) v).getRatingNum());
                            break;
                        case R.id.add_btn:
                            if (upLoading) {
                                ToastUtil.toast("正在上传图片，请稍后");
                                return;
                            }
                            if (PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, CommitActivity.this)) {
                                pullCamera(p);
                            } else {
                                PermissionUtil.applyCamera(CommitActivity.this);
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
            goodTotalStar.setOnClickListener(listener);
            addBtn.setOnClickListener(listener);
            goodComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    commits.get(p).setComment(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }

        private void setData(int pos) {
            p = pos;
            CommitEntity entity = commits.get(pos);
            if (entity == null) return;
            GlideUtil.GlideWithPlaceHolder(CommitActivity.this, entity.getGoodImg()).into(goodImg);
            goodTotalStar.setRating(entity.getScore());
            goodComment.setText(entity.getComment());
            if (entity.getImgUrl() != null && entity.getImgUrl().size() > 0) {
                for (int i = 0; i < entity.getImgUrl().size(); i++) {
                    final RemovableImage removableImage = new RemovableImage(CommitActivity.this);
                    removableImage.setImageFile(entity.getImgUrl().get(i));
                    removableImage.setOnRemovableListener(new RemovableImage.OnRemovableListener() {
                        @Override
                        public void remove(RemovableImage me) {
                            commits.get(p).getImgUrl().remove(me.getImageFileName());
                            me.setVisibility(View.GONE);
                            commentImgContent.removeView(me);
                        }
                    });
                    commentImgContent.addView(removableImage);
                }
            }
        }

        void addGCImg(final String url, final File file) {
            commits.get(p).getImgUrl().add(url);
            final RemovableImage removableImage = new RemovableImage(CommitActivity.this);
            removableImage.setImageFile(file.getAbsolutePath());
            removableImage.setOnRemovableListener(new RemovableImage.OnRemovableListener() {
                @Override
                public void remove(RemovableImage me) {
                    commits.get(p).getImgUrl().remove(url);
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
    }


    private class OrderInfoVH extends RecyclerView.ViewHolder {

        private final CheckBox anonymous;
        private final MyRatingStar goodDesStar;
        private final MyRatingStar goodSerStar;
        private final MyRatingStar goodLogiStar;

        OrderInfoVH(View itemView) {
            super(itemView);
            anonymous = itemView.findViewById(R.id.anonymous);
            goodDesStar = itemView.findViewById(R.id.good_des_star);
            goodSerStar = itemView.findViewById(R.id.good_ser_star);
            goodLogiStar = itemView.findViewById(R.id.good_logi_star);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.anonymous:
                            boolean checked = ((CheckBox) v).isChecked();
                            commitJson.setAnonymous(checked ? 1 : 0);
                            break;
                        default:
                            break;
                    }
                }
            };
            goodDesStar.setUpdateRatingListener(new MyRatingStar.UpdateRatingListener() {
                @Override
                public void updateRating(int rating) {
                    commitJson.setDeptScore(rating);
                }
            });
            goodSerStar.setUpdateRatingListener(new MyRatingStar.UpdateRatingListener() {
                @Override
                public void updateRating(int rating) {
                    commitJson.setServiceScore(rating);
                }
            });
            goodLogiStar.setUpdateRatingListener(new MyRatingStar.UpdateRatingListener() {
                @Override
                public void updateRating(int rating) {
                    commitJson.setShippingScore(rating);
                }
            });
            anonymous.setOnClickListener(listener);

            goodDesStar.setRating(5);
            goodSerStar.setRating(5);
            goodLogiStar.setRating(5);

        }
    }

    private void pullCamera(int p) {
        try {
            file = new File(imageDir, CACHE_FILENAME_PREFIX + System.currentTimeMillis() + "temp.jpg");
            imageUri = FileProvider.getUriForFile(CommitActivity.this, Constant.Provider.getFilePath(), file);
            p_upLoading = p;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, Constant.ImageType.CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.ImageType.CAMERA) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        upLoadFile(file);
                    }
                });
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
                    if (PermissionUtil.hasPermission(PermissionUtil.CAMERA_STRING, CommitActivity.this)) {

                    } else {
                        ToastUtil.toast(this, "缺少相机权限");
                    }
                } else {
                    //用户拒绝授权

                    ToastUtil.toast(this, "缺少相机权限");
                    PermissionUtil.goPermissionManager(CommitActivity.this);
                }
                break;
            default:
                ToastUtil.toast(permsRequestCode + "");
                break;
        }
    }

    /**
     * 上传文件
     */
    public void upLoadFile(final File file) {
        upLoading = true;

        ToastUtil.toast("正在上传图片");
        Bitmap bitmap = ImageHelper.CompressImageToSizeOnFilePath(file.getAbsolutePath(), 300, 300);
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
        builder.addFormDataPart("subFolder", "commit");
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
                    AndroidFileUtil.deleteFile(file);
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
                                if (CommitActivity.this.isDestroyed()) {
                                    return;
                                }
                                rv.scrollToPosition(p_upLoading);
                                RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(p_upLoading);
                                ((CommitVH) (viewHolder)).addGCImg(url, file);
                            }
                        });
                    } else {
                        ToastUtil.toastByCode(baseResult);
                    }
                } else {
                    ToastUtil.toast(MallApplication.getContext(), "上传失败");
                }
            }
        });
    }


    private void doCommit() {
        if (commits == null || commits.size() <= 0) return;
        if (upLoading) {
            ToastUtil.toast("正在上传图片，请稍候");
            return;
        }
        commitJson.setGoodsEvalList(commits);
        commitJson.setDate(TimeUtil.mesToYMD(System.currentTimeMillis()));

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_EVALUATE)
                        , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                .addParam("json", GsonUtil.toJson(commitJson))
                                .getParams());
                BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    ToastUtil.toast("评价完成");
                    finish();
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });

    }

}
