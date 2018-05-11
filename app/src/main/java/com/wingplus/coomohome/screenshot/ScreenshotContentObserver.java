package com.wingplus.coomohome.screenshot;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.util.ToastUtil;

import java.io.File;

public class ScreenshotContentObserver extends ContentObserver {

    private Context mContext;
    private int imageNum;
    private static SnapToken snapToken;

    static ScreenshotContentObserver instance;

    private ScreenshotContentObserver(Context context, SnapToken snapToken) {
        super(null);
        mContext = context;
        ScreenshotContentObserver.snapToken = snapToken;
    }

    public static void startObserve(Context context, SnapToken snapToken) {
        instance = new ScreenshotContentObserver(context, snapToken);
        instance.register();
    }

    public static void stopObserve() {
        instance.unregister();
    }

    private void register() {
        mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, this);
    }

    private void unregister() {
        mContext.getContentResolver().unregisterContentObserver(this);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        String[] columns = {
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATA,
        };
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    MediaStore.MediaColumns.DATE_MODIFIED + " desc");
            if (cursor == null) {
                return;
            }
            int count = cursor.getCount();
            if (imageNum == 0) {
                imageNum = count;
            } else if (imageNum >= count) {
                return;
            }
            imageNum = count;
            if (cursor.moveToFirst()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                long addTime = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
                if (matchAddTime(addTime) && matchPath(filePath)) {
                    if (snapToken != null) {
                        snapToken.doSnap(new File(filePath));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加时间与当前时间不超过1.5s,大部分时候不超过1s。
     *
     * @param addTime 图片添加时间，单位:秒
     */
    private boolean matchAddTime(long addTime) {
        return System.currentTimeMillis() - addTime * 1000 < 1500;
    }

    /**
     * 尺寸不大于屏幕尺寸（发现360奇酷手机可以对截屏进行裁剪）
     */
    private boolean matchSize(String filePath) {
//        DisplayMetrics screenDm = DisplayUtil.getScreenDm(mContext);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
//
//        return screenDm.widthPixels >= options.outWidth && screenDm.heightPixels >= options.outHeight;
        return true;
    }

    /**
     * 已调查的手机截屏图片的路径中带有screenshot
     */
    private boolean matchPath(String filePath) {
        String lower = filePath.toLowerCase();
        return lower.contains("screenshot");
    }

    private void doReport(String filePath) {
        //删除截屏
        ToastUtil.toast("监听到截屏");
        LogUtil.i("截屏", "监听到截屏");
    }

    public interface SnapToken {
        void doSnap(File file);
    }


}