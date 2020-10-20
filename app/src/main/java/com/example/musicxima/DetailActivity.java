package com.example.musicxima;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicxima.adapters.AlbumDetailAdapter;
import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.interfaces.IAlbumDetailCallBack;
import com.example.musicxima.interfaces.IPlayerCallBack;
import com.example.musicxima.interfaces.ISubscriptionCallback;
import com.example.musicxima.interfaces.ISubscriptionPresenter;
import com.example.musicxima.presenters.AlbumDetailPresenter;
import com.example.musicxima.presenters.PlayerPresenter;
import com.example.musicxima.presenters.SubscriptionPresenter;
import com.example.musicxima.utils.ImageBlur;
import com.example.musicxima.utils.LogUtil;
import com.example.musicxima.views.RoundRectImageView;
import com.example.musicxima.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements IAlbumDetailCallBack, UILoader.onRetryClickListener, AlbumDetailAdapter.ItemClickListner, IPlayerCallBack, ISubscriptionCallback {
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private static final String TAG = "DetailActivity";
    private int mCurrentPage = 1;
    private RecyclerView mDetailAlbumRv;
    private AlbumDetailAdapter mDetailAdapter;
    private FrameLayout mDetailListFramlayout;
    private UILoader mUiLoader;
    private int mCurrentId = -1;
    private ImageView mPlayControl;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTrack = null;
    private final static int DEFAULT_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle;
    private TextView mSubBtn;
    private ISubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;
    private boolean mIsSub;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        initPresenter();
        updateSubscribeState();
        updatePlayState(mPlayerPresenter.isPlaying());
        initListener();
        //设置订阅按钮的状态
    }

    private void updateSubscribeState() {
        if (mSubscriptionPresenter != null) {
            mIsSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(mIsSub ?R.string.cancel_sub_tips_text:R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        //这个是专辑详情的presenter
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //播放器的Presenter
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);

        //订阅相关的presenter
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
    }

    private void initListener() {
            mSubBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否已经订阅
                    if (mSubscriptionPresenter != null) {
                        updateSubscribeState();
                        if (mIsSub) {
                            //如果已经//如果已经订阅，那么就显示取消订阅
                            mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                        }else{
                            mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                        }

                    }

                }
            });
            mPlayControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayerPresenter != null) {
                        //判断播放器是否有播放列表
                        boolean hasPlayList = mPlayerPresenter.hasPlayList();
                        if (hasPlayList){
                            handlePlayControl();

                        }else{
                            handleNoPlaylistControl();
                        }
                        
                    }
                }
            });
        
    }

    /**
     * 当播放器里面没有播放内容的时候
     */
    private void handleNoPlaylistControl() {
        mPlayerPresenter.setPlayList(mCurrentTrack,DEFAULT_INDEX);

    }

    private void handlePlayControl() {
        //控制播放器的状态
        if (mPlayerPresenter.isPlaying()) {
            mPlayerPresenter.pause();
        }else{
            mPlayerPresenter.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumDetailPresenter.unRegisterViewCallback(this);
        mPlayerPresenter.unRegisterViewCallback(this);
        mSubscriptionPresenter.unRegisterViewCallback(this);
    }

    private void initView() {

        mDetailAdapter = new AlbumDetailAdapter();
        mDetailListFramlayout = findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup containner) {
                    return createSuccessView(containner);
                }
            };
        }
        mDetailListFramlayout.removeAllViews();
        mDetailListFramlayout.addView(mUiLoader);
        mUiLoader.setOnRetryClickListener(this);

        mLargeCover = findViewById(R.id.iv_larger_cover);
        mSmallCover = findViewById(R.id.viv_small_cover);
        mAlbumTitle = findViewById(R.id.tv_album_title);
        mAlbumAuthor = findViewById(R.id.tv_author_info);
        //控制播放的图标,专辑详情界面
        mPlayControl = findViewById(R.id.play_btn);
        mPlayControlTips = findViewById(R.id.paly_control_text);
        mPlayControlTips.setSelected(true);
        //设置订阅按钮
        mSubBtn = findViewById(R.id.detail_sub_btn);
    }
    private boolean isLoadedMore = false;

    private View createSuccessView(ViewGroup containner) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, containner, false);
        mDetailAlbumRv = detailListView.findViewById(R.id.recycle_album_detail);
        //找到上拉加载，下拉刷新控件布局
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        //设置布局管理器
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mDetailAlbumRv.setLayoutManager(linearLayout);//给recyleview设置线性布局
        //设置适配器

        mDetailAlbumRv.setAdapter(mDetailAdapter);
        //设置间距
        mDetailAlbumRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mDetailAdapter.setItemClickListner(this);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);

                BaseApplication.getsHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                isLoadedMore = true;
               //去加载更多内容
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                }
            }
        });
        return detailListView;
    }


    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (isLoadedMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            isLoadedMore = false;

        }
        this.mCurrentTrack = tracks;
        //判断数据结果，根据结果控制ui
        if (tracks==null || tracks.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIstatus.EMPTY);

            }
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIstatus.SUCCESS);
        }
        //更新设置ui数据
        mDetailAdapter.setData(tracks);

    }

    @Override
    public void onNetWorkErrors(int errorCode, String errorMessage) {
        //发生错误
        mUiLoader.updateStatus(UILoader.UIstatus.NETWORK_ERROR);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum = album;
        int albumId = (int) album.getId();
        //获取专辑的内容
        mCurrentId = albumId;
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail(albumId, mCurrentPage);
        }


        //显示loding
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIstatus.LOADING);

        }
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        //做成毛玻璃样式
        if (mLargeCover != null && null != mLargeCover) {
            Picasso.get().load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        //到这里才说明是有图片的
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError(Exception e) {
                    LogUtil.d(TAG, "onError");
                }
            });
        }
        Picasso.get().load(album.getCoverUrlSmall()).into(mSmallCover);
    }

    @Override
    public void onLoadedMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this, "成功加载"+size+"条", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"没有更多节目",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void RetryClick() {
        //这里点击重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail(mCurrentId, mCurrentPage);
        }

    }

    @Override
    public void onItemClick(List<Track> detaillData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getInstance();
        playerPresenter.setPlayList(detaillData,position);
        //Todo：跳转到播放器界面
        Intent intent = new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
      updatePlayState(false);
    }

    /**
     * 根据播放状态修改图标
     * @param playing
     */
    private void updatePlayState(boolean playing) {
        if (mPlayControl != null && mPlayControlTips != null) {
            mPlayControl.setImageResource(playing?R.drawable.selector_play_control_pause:R.drawable.selector_play_control_play);
            if(!playing){
                mPlayControlTips.setText(R.string.click_play_tips_text);
            }else{
                mPlayControlTips.setText(mCurrentTrackTitle);
            }
        }
    }


    @Override
    public void onPlayStop() {
        updatePlayState(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> trackList) {

    }

    @Override
    public void onplayModeChanger(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinish() {

    }

    @Override
    public void onTrackUpdate(Track track, int position) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTips != null) {
                mPlayControlTips.setText(mCurrentTrackTitle);
            }

        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功
            mSubBtn.setText(R.string.cancel_sub_tips_text);

        }
        String tipsText = getString(isSuccess?R.string.subSuccess:R.string.subfail);

        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功
            mSubBtn.setText(R.string.sub_tips_text);
        }
        String tipsText = getString(isSuccess?R.string.CancelSuccess:R.string.failDete);
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {
       //不需要处理
    }

    @Override
    public void onSubTooMany() {
        Toast.makeText(this, "订阅太多内容了", Toast.LENGTH_SHORT).show();
    }
}