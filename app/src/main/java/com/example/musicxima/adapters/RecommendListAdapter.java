package com.example.musicxima.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicxima.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerViewHolder> {
    private List<Album> mData = new ArrayList<>();

    @NonNull
    @Override
    public InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemViews = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerViewHolder(itemViews);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        //这里设置数据,把当前的位置设置给item
        holder.itemView.setTag(position);
        holder.setDatas(mData.get(position));

    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新ui
        notifyDataSetChanged();
    }

    public class InnerViewHolder extends RecyclerView.ViewHolder{
        public InnerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setDatas(Album album) {
            //找到各个控件，设置数据
            //cover
            ImageView albumCover = (ImageView) itemView.findViewById(R.id.album_cover);
            //title
            TextView albulmTitle = (TextView) itemView.findViewById(R.id.album_title_txt);
            //description
            TextView albumDescription = itemView.findViewById(R.id.album_description);
            TextView albumPlayCount = itemView.findViewById(R.id.album_play_count);
            TextView albumContentSize = itemView.findViewById(R.id.album_content_size);

            albulmTitle.setText(album.getAlbumTitle());
            albumDescription.setText(album.getAlbumIntro());
            albumPlayCount.setText(album.getPlayCount()+"");
            albumContentSize.setText(album.getIncludeTrackCount()+"");

            Picasso.get().load(album.getCoverUrlLarge()).into(albumCover);



        }
    }
}
