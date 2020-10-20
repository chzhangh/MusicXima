package com.example.musicxima.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.musicxima.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayerTrackPagerAdapter extends PagerAdapter {
    private List<Track> mTracks = new ArrayList<>();

    @Override
    public int getCount() {
        return mTracks.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager, container, false);
        container.addView(view);
        //设置数据，找到控件
        ImageView item = view.findViewById(R.id.track_pager_item);
        //
        Track track = mTracks.get(position);
        String coverUrlLarge = track.getCoverUrlLarge();
        Picasso.get().load(coverUrlLarge).into(item);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
      container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> trackList) {
        mTracks.clear();
        mTracks.addAll(trackList);
        notifyDataSetChanged();//更新数据

    }
}
