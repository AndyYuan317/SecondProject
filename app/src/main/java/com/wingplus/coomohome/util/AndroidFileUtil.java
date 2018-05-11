package com.wingplus.coomohome.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.danikula.videocache.file.Md5FileNameGenerator;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.DataConfig;
import com.wingplus.coomohome.config.LogUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author leaffun.
 *         Create on 2017/9/28.
 */
public class AndroidFileUtil {

    public static String getVideoPath(String url, Context context) {

        File adDataCache = context.getExternalCacheDir();

        File vCache = new File(adDataCache, "/video-cache");
        if (!vCache.isDirectory()) {
            return "";
        } else {
            File videoPath = new File(vCache, new Md5FileNameGenerator().generate(url));
            if (videoPath.isFile() && videoPath.length() > 0) {
                String path = videoPath.getPath();
                LogUtil.i("AndroidFileUtil--getVideoPath", url + " video-path-is: " + path);
                return path;
            }
        }
        return "";
    }

    /**
     * 存储登录token
     *
     * @param string
     * @param filePath
     * @param fileName
     */
    public static void writeStringToFile(String string, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            file = new File(filePath, fileName);
            if (file.exists()) {
                boolean delete = file.delete();
                LogUtil.i("删除文件", delete + "");
            }

            File dir = new File(filePath);
            if (!dir.exists() && !dir.isDirectory()) {//判断文件夹目录是否存在
                boolean mkdirs = dir.mkdirs();
                LogUtil.i("创建文件夹", mkdirs + "");
            }

            LogUtil.i("token write file path", file.getAbsolutePath());

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void deleteFile(File file){
        if (file.exists()) {
            boolean delete = file.delete();
            LogUtil.i("删除文件", delete + "");
        }
    }

    /**
     * 删除token
     *
     * @param filePath
     * @param fileName
     */
    private static void deleteToken(File filePath, String fileName) {
        File file = new File(filePath, fileName);
        if (file.exists()) {
            boolean delete = file.delete();
            LogUtil.i("删除token", delete + "");
            if(delete){
                String absolutePath = MallApplication.getContext().getCacheDir().getAbsolutePath();
                AndroidFileUtil.writeStringToFile("true", absolutePath, DataConfig.PUSH_FILE_NAME);
            }
        }
    }

    public static void deleteToken() {
        deleteToken(new File(MallApplication.getContext().getCacheDir().getAbsolutePath() + "/"), DataConfig.TOKEN_FILE_NAME);
    }

    /**
     * 读取token
     *
     * @param fileName
     * @return
     */
    public static String readFileByLines(String fileName) {
        LogUtil.i("token read file path: ", fileName);
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            if (!file.exists()) {
                return "";
            }
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * <br>功能简述:4.4及以上获取图片的方法
     * <br>功能详细描述:
     * <br>注意:
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
