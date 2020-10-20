package com.example.musicxima.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicxima.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.ViewH> {
    private List<QueryResult> mQueryResults = new ArrayList<>();
    private ItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public SearchRecommendAdapter.ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tecommend, parent, false);
        return new ViewH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendAdapter.ViewH holder, int position) {
        TextView text = holder.itemView.findViewById(R.id.search_recommend_item);
        final QueryResult queryResult = mQueryResults.get(position);
        text.setText(queryResult.getKeyword());
        //设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemclick(queryResult.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQueryResults.size();
    }

    public void setData(List<QueryResult> keywordList) {
        mQueryResults.clear();
        mQueryResults.addAll(keywordList);
        notifyDataSetChanged();

    }

    public class ViewH extends RecyclerView.ViewHolder {

        public ViewH(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener{
        void onItemclick(String keyword);
    }
}
