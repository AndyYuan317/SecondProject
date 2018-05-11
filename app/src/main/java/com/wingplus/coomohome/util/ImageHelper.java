package com.wingplus.coomohome.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.wingplus.coomohome.config.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片操作工具类
 */
public class ImageHelper {
    /**
     * 缩小图片到尺寸以内，并尽量降低图片大小小于100K
     */
    public static Bitmap CompressImageToSize(Bitmap image, int height, int width) {
        LogUtil.e("原始的图片申请内存大小", "" + image.getAllocationByteCount());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        baos.reset();
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
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
        try {
            isBm.reset();
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CompressImage(bitmap);
    }

    /**
     * 缩小图片到尺寸以内，并尽量降低图片大小小于100K
     */
    public static Bitmap CompressImageToSizeOnFilePath(String imagePath, int height, int width) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, opt);

        int w = opt.outWidth;
        int h =  opt.outHeight;
        int be = 1;
        if (w > h && w > width) {
            be = w / width;
        } else if (w < h && h > height) {
            be = h / height;
        }
        if (be <= 0)
            be = 1;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = be;
        Bitmap bm = BitmapFactory.decodeFile(imagePath, newOpts);
        return CompressImage(bm);
    }



    /**
     * 降低图片质量来压缩图片，尽量小于100K
     */
    public static Bitmap CompressImage(Bitmap image) {
        if(image == null)return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100 && options >= 0) {
            options -= 30;
            baos.reset();
            // 这种方法保存了图片所有的像素，不能无限压缩图片大小
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        try {
            isBm.reset();
            isBm.close();
            LogUtil.e("最后压缩的图片申请内存大小", "" + bitmap.getAllocationByteCount());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.gc();
                System.runFinalization();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 降低图片质量来压缩图片，尽量小于32K
     */
    public static Bitmap CompressImageToBytes(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 90, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 32 && options >= 0) {
            options -= 30;
            baos.reset();
            // 这种方法保存了图片所有的像素，不能无限压缩图片大小
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        try {
            isBm.reset();
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
