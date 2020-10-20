package com.example.musicxima.interfaces;

import com.example.musicxima.base.IBasePresenter;
import com.example.musicxima.presenters.AlbumDetailPresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailCallBack> {
    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId,int page);
}
