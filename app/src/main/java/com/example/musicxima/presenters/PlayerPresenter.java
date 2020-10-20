package com.example.musicxima.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.musicxima.DataBase.XimalayaApi;
import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.interfaces.IPlayerCallBack;
import com.example.musicxima.interfaces.IPlayerPresenter;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {
    private static final String TAG = "PlayerPresenter";

    private XmPlayerManager mPlayerManager;
    private List<IPlayerCallBack> mIPlayerCallBacks = new ArrayList<>();
    private Track mCurrentTrack;
    private int mCurrentIndex;
    private final SharedPreferences mPlayModeSp;
    //定义常量保存
    private static  final int PLAY_MODEL_LIST_INT = 0;
    private static  final int PLAY_MODEL_LIST_LOOP_INT = 1;
    private static  final int PLAY_MODEL_LIST_RANDOM_INT = 2;
    private static  final int PLAY_MODEL_LIST_SINGLE_LOOP = 3;

    //sp'name key and name
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    public static final String PLAY_MODE_SP_KEY = "Current_PlayMode";
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    private boolean isReverse = false;
    private int mCurrentProgress = 0;
    private int mCurrentDuration = 0;

    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //注册广告物料监听
        mPlayerManager.addAdsStatusListener(this);
        //注册实现跟播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);


    }
    private static PlayerPresenter sInstance;
    public static PlayerPresenter getInstance(){
        synchronized (PlayerPresenter.class){
            if (sInstance == null) {
                sInstance = new PlayerPresenter();
            }
        }
        return  sInstance;
    }

    private boolean isPlayListSet = false;//默认是没有播放列表集合的
    public void setPlayList(List<Track> playList,int playIndex){
        isPlayListSet = true;
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(playList,playIndex);//养成判断空的习惯
            mCurrentTrack = playList.get(playIndex);
            mCurrentIndex = playIndex;

        }else{
            LogUtil.d(TAG,"mPlayerManager is null");
        }
    }



    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    public boolean hasPlayList(){
        /*List<Track> playlist = mPlayerManager.getPlayList();*/
        return isPlayListSet;
    }

    @Override
    public void playPrevious() {
      if (mPlayerManager != null){
          mPlayerManager.playPre();
      }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            //通知UI更新播放模式
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onplayModeChanger(mode);
            }
            //保存到
            SharedPreferences.Editor edit = mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            edit.commit();

        }
    }
    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_LIST_RANDOM_INT;
            case PLAY_MODEL_SINGLE_LOOP:
               return PLAY_MODEL_LIST_SINGLE_LOOP;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getPlayModeByInt(int index){
        switch (index){
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_LIST_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP;
        }
        return PLAY_MODEL_LIST;
    }
    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onListLoaded(playList);
            }
        }

    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到index位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);

    }

    @Override
    public boolean isPlaying() {
        //返回当前是否正在播放
        return mPlayerManager.isPlaying();
    }

    /**
     * 播放列表逆序切换
     */

    @Override
    public void reversePlayList() {
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        //
        isReverse = !isReverse;

        mCurrentIndex = playList.size()-1-mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);

        //更新ui
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
           iPlayerCallBack.onListLoaded(playList);
            iPlayerCallBack.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerCallBack.updateListOrder(isReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //要获取专辑列表的内容
        XimalayaApi instance = XimalayaApi.getInstance();
        instance.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                if (trackList != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks,0);
                    mCurrentTrack = tracks.get(0);
                    mCurrentIndex = 0;
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(), "请求数据错误。。", Toast.LENGTH_SHORT).show();
            }
        },(int)id,1);
        //把专辑内容设置给播放器
        //播放了。。。

    }

    @Override
    public void registerViewCallback(IPlayerCallBack m) {
        if (!mIPlayerCallBacks.contains(m)) {
            mIPlayerCallBacks.add(m);
        }
        getPlayList();
        //通知当前节目
        m.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        m.onProgressChange(mCurrentProgress,mCurrentDuration);
        //更新状态
        handlePlayState(m);
        //从sp里面拿
        int modeIndex = mPlayModeSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getPlayModeByInt(modeIndex);
        m.onplayModeChanger(mCurrentPlayMode);

    }

    private void handlePlayState(IPlayerCallBack m) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        //根据状态调用接口的方法
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            m.onPlayStart();
        }else{
            m.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallBack m) {
        if (mIPlayerCallBacks != null) {
            mIPlayerCallBacks.remove(m);
        }

    }
    //广告相关的回调方法===================================

    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo");

    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG,"onError what ->"+what+"extra->"+extra);
    }
    //end========================================
    //------------------------------------------播放器状态相关的接口回调实现==========================
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete。。");

    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared");
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED) {
            //播放器准备完了可以播放了
            mPlayerManager.play();
        }
    }
   //切歌
    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {

        if (lastModel != null) {
            LogUtil.d(TAG,"onSoundSwitch"+lastModel.getKind());
        }
        if (curModel != null) {

            LogUtil.d(TAG,"onSoundSwitch-----"+curModel.getKind());
        }
        LogUtil.d(TAG,"onSoundSwitch");
        //curModel 代表的是当前播放的内容。通过getkind来获取他是什么样的类型 track表示的track类型
        //第一种写法:不推荐
        /*if ("track".equals(curModel.getKind())) {
            Track currentTrack = (Track) curModel;
            LogUtil.d(TAG,currentTrack.getTrackTitle());
        }*/
        //
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            //保存播放记录
            HistoryPresenter historyPresenter = HistoryPresenter.getInstance();
            historyPresenter.addHistory(currentTrack);
            //LogUtil.d(TAG,currentTrack.getTrackTitle());
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onTrackUpdate(mCurrentTrack,mCurrentIndex);

            }
        }
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart...");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop...");
    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG,"onBufferProgress"+progress);
    }

    @Override
    public void onPlayProgress(int currentPosition, int duration) {
        this.mCurrentProgress = currentPosition;
        this.mCurrentDuration = duration;
        //单位是毫秒
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onProgressChange(currentPosition,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"e----->"+e);
        return false;
    }
    //end========================================
}
