package com.wingplus.coomohome.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.web.entity.GoodShow;

import java.util.List;

public class LikeVH extends RecyclerView.ViewHolder {
    private final LikeAdapter adapter;

    public LikeVH(View itemView, Activity activity, String name) {
        super(itemView);
        RecyclerView rv = itemView.findViewById(R.id.rv);
        GridLayoutManager manager = new GridLayoutManager(activity, 2);
        rv.setLayoutManager(manager);
        adapter = new LikeAdapter(null, activity);
        rv.setAdapter(adapter);

        TextView title = itemView.findViewById(R.id.title);
        title.setText(name == null ? "猜你喜欢" : name);
    }

    public void refreshLikeData(List<GoodShow> list) {
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }
}
