package com.example.musicxima.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailCallBack {
    /**
     * 详情页面加载出来了
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 网络错误
     */
    void onNetWorkErrors(int errorCode, String errorMessage);

    /**
     * 把Album传给UI使用
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 加載更多的結果
     * @param size true 表示加载成功，false表示加载失败
     */

    void onLoadedMoreFinished(int size);

    /**
     * 下拉加载更多的结果
     * @param size
     */
    void onRefreshFinished(int size);

}
