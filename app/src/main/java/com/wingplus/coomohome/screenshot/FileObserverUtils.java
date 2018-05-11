package com.wingplus.coomohome.screenshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.FileObserver;


import com.wingplus.coomohome.config.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileObserverUtils {
    private static List<FileObserver> fileObservers;
    private static ISnapShotCallBack snapShotCallBack;
    private static String lastShownSnapshot;
    private static final int MAX_TRYS = 5;

    public static void setSnapShotCallBack(ISnapShotCallBack callBack) {
        snapShotCallBack = callBack;
        initFileObserver();
    }

    private static void initFileObserver() {
        String SNAP_SHOT_FOLDER_PATH = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_PICTURES
                + File.separator + "Screenshots" + File.separator;

        String SNAP_SHOT_FOLDER_PATH2 = Environment.getExternalStorageDirectory()
                + File.separator + "DCIM"
                + File.separator + "Screenshots" + File.separator;


        add(SNAP_SHOT_FOLDER_PATH);
        add(SNAP_SHOT_FOLDER_PATH2);
    }

    public static void releaseFileObserver(){
        if (fileObservers != null && fileObservers.size() > 0) {
            fileObservers.clear();
        }
    }

    private static void add(final String screenPath) {
        if(fileObservers == null){
            fileObservers = new ArrayList<>();
        }
        LogUtil.i("添加监听路径", screenPath);
        fileObservers.add(new FileObserver(screenPath, FileObserver.ALL_EVENTS) {
            @Override
            public void onEvent(int event, String path) {
                handleEvent(screenPath, event, path);
            }
        });
    }

    private static void handleEvent(String screenPath, int event, String path) {
        if (null != path && event == FileObserver.CREATE && (!path.equals(lastShownSnapshot))) {
            lastShownSnapshot = path; // 有些手机同一张截图会触发多个CREATE事件，避免重复展示

            String snapShotFilePath = screenPath + path;

            int tryTimes = 0;
            boolean getBmp = false;
            while (true) {
                try { // 收到CREATE事件后马上获取并不能获取到，需要延迟一段时间
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    LogUtil.i("截屏捕捉", "开始尝试第" + tryTimes + "次");
                    Bitmap bitmap = BitmapFactory.decodeFile(snapShotFilePath);

                    for (int i = 0; i < 100; i++) {
                        int x = (int) (Math.random() * bitmap.getWidth());
                        int y = (int) (Math.random() * bitmap.getHeight());
                        int clr = bitmap.getPixel(x, y);
                        int red = (clr & 0x00ff0000) >> 16;  //取高两位
                        int green = (clr & 0x0000ff00) >> 8; //取中两位
                        int blue = clr & 0x000000ff; //取低两位
                        LogUtil.i("截屏捕捉", "随机点位[" + x + "---" + y + "]" + red + "-" + green + "-" + blue);


                        if (red + green + blue != 0) {
                            getBmp = true;
                            break;
                        }
                    }
                    if (getBmp) {
                        break;
                    } else {
                        tryTimes++;
                        if (tryTimes >= MAX_TRYS) { // 尝试MAX_TRYS次失败后，放弃
                            return;
                        }
                    }
                } catch (Exception e) {
                    getBmp = false;
                    e.printStackTrace();
                    tryTimes++;
                    if (tryTimes >= MAX_TRYS) { // 尝试MAX_TRYS次失败后，放弃
                        return;
                    }
                }
            }

            int lock = 100;
            if (!getBmp) {
                lock = 1000;
            }

            try { // 收到CREATE事件后马上获取并不能获取到，需要延迟一段时间
                Thread.sleep(lock);
            } catch (Exception e) {
                e.printStackTrace();
            }
            snapShotCallBack.snapShotTaken(snapShotFilePath);
        }
    }

    public static void startSnapshotWatching() {
        if (null == snapShotCallBack) {
            throw new ExceptionInInitializerError("Call FileObserverUtils.setSnapShotCallBack first to setup callback!");
        }

        if (fileObservers != null && fileObservers.size() > 0) {
            for (FileObserver fileObserver : fileObservers) {
                fileObserver.startWatching();
            }
        }
    }

    public static void stopSnapshotWatching() {
        if (null == snapShotCallBack) {
            throw new ExceptionInInitializerError("Call FileObserverUtils.setSnapShotCallBack first to setup callback!");
        }

        if (fileObservers != null && fileObservers.size() > 0) {
            for (FileObserver fileObserver : fileObservers) {
                fileObserver.stopWatching();
            }
        }
    }

    public static void releaseCallBack() {
        if (snapShotCallBack != null) {
            snapShotCallBack = null;
        }
    }

}
