package com.wingplus.coomohome.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wingplus.coomohome.R;

/**
 * @author leaffun.
 *         Create on 2017/8/26.
 */
public class StoryFragment extends BaseFragment {

    private LayoutInflater myInflater;
    private View rootView;
    private AppBarLayout appBar;
    private Toolbar toolBar;
    private CollapsingToolbarLayout cTBar;


    public StoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflater = inflater;
        if (rootView == null) {
            rootView = myInflater.inflate(R.layout.fragment_story, container, false);
        }
        initView();
        return rootView;
    }

    public void initView(){
        appBar = rootView.findViewById(R.id.abl);
        toolBar = rootView.findViewById(R.id.tb);
        cTBar = rootView.findViewById(R.id.ctl);
    }


    @Override
    public void setStatusBarColor(Activity activity) {
//        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.green));
//        StatusBarUtil.setStatusBarColorForCollapsingToolbar(getActivity(),appBar,cTBar,toolBar,getResources().getColor(R.color.green));
    }
}
