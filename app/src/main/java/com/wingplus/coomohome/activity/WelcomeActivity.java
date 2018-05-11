package com.wingplus.coomohome.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.CrashHandler;
import com.wingplus.coomohome.config.DataConfig;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.AndroidFileUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.JPushUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.LoginResult;

import java.io.File;
import java.io.FilenameFilter;

import cn.jpush.android.api.JPushInterface;

import static com.wingplus.coomohome.config.DataConfig.CACHE_FILENAME_PREFIX;

/**
 * 欢迎页
 *
 * @author leaffun.
 *         Create on 2017/8/16.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i("WelcomeActivity onCreate time", System.currentTimeMillis() + "");
        setContentView(R.layout.activity_welcome);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        clearLog();
                        clearCache();
                        String token = AndroidFileUtil.readFileByLines(getCacheDir().getAbsolutePath() + "/" + DataConfig.TOKEN_FILE_NAME);
                        LogUtil.i("token", token);
                        if (token.length() > 0) {
                            String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_USER_INFO),
                                    new ParamsBuilder()
                                            .addParam("token", token)
                                            .getParams());
                            LoginResult result = GsonUtil.fromJson(rs, LoginResult.class);
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                RuntimeConfig.user = result.getData();
                                token = RuntimeConfig.user.getToken();
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                            JPushUtil.pushRegistrationID(token);
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    start();
                                }
                            });

                        } else {
                            JPushUtil.pushRegistrationID(token);
                            start();
                        }
                    }
                });
            }
        }, 1000);
    }

    private void start() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.putExtra("from", "WelcomeActivity");
        startActivity(intent);
        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
        WelcomeActivity.this.finish();
    }


    private static void clearCache() {
        final FilenameFilter cacheFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(CACHE_FILENAME_PREFIX);
            }
        };

        try {
            File cacheDir = MallApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            assert cacheDir != null;
            final File[] files = cacheDir.listFiles(cacheFileFilter);
            if(files == null) return;
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static void clearLog() {
        final FilenameFilter cacheFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(CrashHandler.FILE_NAME);
            }
        };

        try {
            File logDir = new File(CrashHandler.PATH);
            final File[] files = logDir.listFiles(cacheFileFilter);
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
