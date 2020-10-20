package com.example.musicxima.interfaces;

import com.example.musicxima.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryPresenter extends IBasePresenter<IHistoryCallBack> {
    /**
     * 获取历史
     */
    void listHistories();

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    void delHistory(Track track);

    void cleanHistory(Track track);

}
