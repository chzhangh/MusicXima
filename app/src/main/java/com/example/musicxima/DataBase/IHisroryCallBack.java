package com.example.musicxima.DataBase;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHisroryCallBack {
    /**
     * 添加历史结果
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史结果
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);


    /**
     * 历史数据加载的结果
     * @param trackList
     */
    void onHistoryLoaded(List<Track> trackList);

    /**
     * 清除历史的结果
     */
    void onHistoryClean(boolean isSuccess);
}
