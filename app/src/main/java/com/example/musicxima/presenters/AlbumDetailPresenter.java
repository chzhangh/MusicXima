package com.example.musicxima.presenters;

import com.example.musicxima.DataBase.XimalayaApi;
import com.example.musicxima.interfaces.IAlbumDetailCallBack;
import com.example.musicxima.interfaces.IAlbumDetailPresenter;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailCallBack> mDetailCallBacks = new ArrayList<>();
    private List<Track> mTrackList = new ArrayList<>();
    private Album mTargetAlbum = null;
    //当前的专辑id
    private int mCurrentAlbumId = -1;
    //当前页面
    private int mCurrentPageIndex = 0;

    private AlbumDetailPresenter(){}
    private  static AlbumDetailPresenter sInstance = null;
    public static AlbumDetailPresenter getInstance(){
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class){
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }

            }
        }
        return sInstance;
    }
    @Override
    public void pull2RefreshMore() {
    }

    @Override
    public void loadMore() {
      //去加载更多内容
        mCurrentPageIndex++;
        //传入true表示结果被
        doLoaded(true);

    }

    private void doLoaded(final boolean isLoadMore){
        XimalayaApi ximalayaApi = XimalayaApi.getInstance();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG,""+tracks.size());
                    if(isLoadMore){
                        //上拉加载结果放到后面去
                        mTrackList.addAll(tracks);//将获取到的专辑详情页面加载到当前的集合中保存
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    }else{
                        //下拉刷新，结果放到列表的前面去
                        mTrackList.addAll(0,tracks);
                    }
                    handleAlbumDetailResult(mTrackList);
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                if (isLoadMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG,"errorCode -->"+errorCode);
                LogUtil.d(TAG,"errormessage -->"+errorMessage);
                handleError(errorCode,errorMessage);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

    /**\
     * 处理加载更多的结果
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailCallBack detailCallBack : mDetailCallBacks) {
            detailCallBack.onLoadedMoreFinished(size);
        }
    }

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTrackList.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        doLoaded(false);
    }

    /**
     * 如果发生错误就通知UI
     * @param errorCode
     * @param errorMessage
     */
    private void handleError(int errorCode, String errorMessage) {
        for (IAlbumDetailCallBack mDetailCallBack : mDetailCallBacks) {
            mDetailCallBack.onNetWorkErrors(errorCode,errorMessage);
        }
    }

    private void handleAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailCallBack mDetailCallBack : mDetailCallBacks) {
            mDetailCallBack.onDetailListLoaded(tracks);
        }
    }



    @Override
    public void registerViewCallback(IAlbumDetailCallBack detailCallBack) {
        if (!mDetailCallBacks.contains(detailCallBack)) {
            mDetailCallBacks.add(detailCallBack);
        }
        if (mTargetAlbum != null) {
            detailCallBack.onAlbumLoaded(mTargetAlbum);
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailCallBack callBack) {
        if (mDetailCallBacks.contains(callBack)) {
            mDetailCallBacks.remove(callBack);
        }

    }

    public void setTagAlbum(Album album){
        this.mTargetAlbum = album;
    }
}
