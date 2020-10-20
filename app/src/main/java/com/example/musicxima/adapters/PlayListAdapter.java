package com.example.musicxima.adapters;

import android.content.ContentUris;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicxima.R;
import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {
    private List<Track> mTrackList = new ArrayList<>();
    private ImageView mImgBG;
    private TextView trackTitleTv;
    private int playIndex = 0;
    private SobPopWindow.PlayListItemClickListener mItemClickListner;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.play_list_item, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //设置数据
        Track track = mTrackList.get(position);
        mImgBG = holder.itemView.findViewById(R.id.play_icon);
        trackTitleTv = holder.itemView.findViewById(R.id.track_title);
        trackTitleTv.setText(track.getTrackTitle());
        trackTitleTv.setTextColor(
                BaseApplication.getAppContext().getResources().getColor( playIndex == position?R.color.main_color:R.color.gray));
        mImgBG.setVisibility((playIndex==position)?View.VISIBLE:View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListner.onPlayListItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    public void setData(List<Track> data) {
        //设置数据更新列表
        mTrackList.clear();
        mTrackList.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        playIndex = position;
        notifyDataSetChanged();

    }

    public void setOnItemClickListner(SobPopWindow.PlayListItemClickListener listener) {
        this.mItemClickListner = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
