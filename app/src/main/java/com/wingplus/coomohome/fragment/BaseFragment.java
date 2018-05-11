package com.wingplus.coomohome.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * @author leaffun.
 *         Create on 2017/8/27.
 */
public class BaseFragment extends Fragment {

    public void setStatusBarColor(Activity activity){

    }

    public void setContentItem(int i){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            System.gc();
            System.runFinalization();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
