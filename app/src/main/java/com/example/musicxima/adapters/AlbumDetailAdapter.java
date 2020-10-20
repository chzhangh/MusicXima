package com.example.musicxima.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicxima.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AlbumDetailAdapter extends RecyclerView.Adapter<AlbumDetailAdapter.InnerHolder> {
    private List<Track> mDetaillData = new ArrayList<>();
    //格式化时间
     private SimpleDateFormat mSimpleDataFormat = new SimpleDateFormat("yyyy-MM-dd");
     private SimpleDateFormat mDurationDataFormat = new SimpleDateFormat("mm:ss");
     private ItemClickListner mItemClickListner = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View detailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_layout, parent, false);
        return new InnerHolder(detailView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InnerHolder holder, final int position) {
      //设置数据
        Track track = mDetaillData.get(position);
        holder.orderTv.setText((position+1)+"");
        holder.titleTv.setText(track.getTrackTitle());
        holder.playcountTv.setText(track.getPlayCount()+"");
        String Duration = mDurationDataFormat.format(track.getDuration()*1000);
        holder.durationTV.setText(Duration);
        String timer = mSimpleDataFormat.format(track.getUpdatedAt());
        holder.updateTimeTv.setText(timer);
        
        //设置item的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(holder.itemView.getContext(), "you clicked"+position+"item", Toast.LENGTH_SHORT).show();
                if (mItemClickListner != null) {
                    //参数需要有列表和位置
                    mItemClickListner.onItemClick(mDetaillData,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDetaillData.size();
    }

    public void setData(List<Track> tracks) {
        //清除原来的数据
        mDetaillData.clear();
        //添加新的数据
        mDetaillData.addAll(tracks);
        notifyDataSetChanged();//更新ui
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private TextView orderTv,titleTv,playcountTv,durationTV,updateTimeTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            orderTv = itemView.findViewById(R.id.order_text);
            titleTv = itemView.findViewById(R.id.detail_item_title);
            playcountTv = itemView.findViewById(R.id.detail_paly_times);
            durationTV = itemView.findViewById(R.id.detail_item_duration);
            updateTimeTv = itemView.findViewById(R.id.detail_item_update_time);
        }
    }
    public void setItemClickListner(ItemClickListner listner){
        this.mItemClickListner = listner;
    }

    public interface ItemClickListner{
        void onItemClick(List<Track> detaillData, int position);
    }
}
