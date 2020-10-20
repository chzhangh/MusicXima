package com.example.musicxima.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicxima.PlayerActivity;
import com.example.musicxima.R;
import com.example.musicxima.adapters.AlbumDetailAdapter;
import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.base.BaseFragment;
import com.example.musicxima.interfaces.IHistoryCallBack;
import com.example.musicxima.presenters.HistoryPresenter;
import com.example.musicxima.presenters.PlayerPresenter;
import com.example.musicxima.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallBack, AlbumDetailAdapter.ItemClickListner {

    private UILoader mUiLoader;
    private RecyclerView mRecyclerView;
    private AlbumDetailAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private PlayerPresenter mPlayerPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, final ViewGroup container) {
        FrameLayout v = (FrameLayout) inflater.inflate(R.layout.fragment_history,container, false);
        mPlayerPresenter = PlayerPresenter.getInstance();

        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup containner) {
                    return createSuccessView(container);
                }
            };
        }else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        //
        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIstatus.LOADING);
        mHistoryPresenter.listHistories();
        v.addView(mUiLoader);
        return v;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, container, false);
        mRecyclerView = successView.findViewById(R.id.history_list);
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableOverScroll(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mTrackListAdapter = new AlbumDetailAdapter();
        mTrackListAdapter.setItemClickListner(this);
        mRecyclerView.setAdapter(mTrackListAdapter);
        //设置间距
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        return successView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        //更新数据
        mTrackListAdapter.setData(tracks);
        mUiLoader.updateStatus(UILoader.UIstatus.SUCCESS);
    }

    @Override
    public void onItemClick(List<Track> detaillData, int position) {
        //设置播放器的数据
        mPlayerPresenter.setPlayList(detaillData,position);
        //Todo：跳转到播放器界面
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }
}