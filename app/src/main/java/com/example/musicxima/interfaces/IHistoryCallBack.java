package com.example.musicxima.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallBack {
    /**
     * 历史内容加载
     * @param tracks
     */
    void onHistoryLoaded(List<Track> tracks);
}
