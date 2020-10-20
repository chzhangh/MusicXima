package com.example.musicxima.DataBase;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistroryDao {
    /**
     * 设置回调接口
     * @param callBack
     */
    void setCallBack(IHisroryCallBack callBack);

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    /***
     * 删除历史
     */
    void delHistory(Track track);

    /**
     * 清除历史
     * @param track
     */
    void clearHistory(Track track);

    /**
     * 获取历史内容
     */
    void listHistory();
}
