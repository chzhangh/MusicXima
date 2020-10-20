package com.example.musicxima.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallBack {
    /**
     *开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */

    void onPlayError();

    /**
     * 上下首播放，viewpager的切换
     */

    void onNextPlay(Track track);
    void onPrePlay(Track track);

    /**
     * 播放列表播放数据加载完成
     * @param trackList 播放器列表数据
     */
    void onListLoaded(List<Track> trackList);

    /**
     * 播放器模式改变
     * @param playMode
     */

    void onplayModeChanger(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条改变
     * @param currentProgress
     * @param total
     */

    void onProgressChange(int currentProgress,int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告加载完成
     */
    void onAdFinish();

    /**
     * 更新当前的节目
     *
     */
    void onTrackUpdate(Track track,int position);

    /**
     * 通知ui更新播放列表
     * @param isReverse
     */

    void updateListOrder(boolean isReverse);
}
