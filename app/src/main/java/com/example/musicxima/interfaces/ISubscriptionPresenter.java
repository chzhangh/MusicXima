package com.example.musicxima.interfaces;

import com.example.musicxima.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅的上限是不能超过100个
 *
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback>{
    /**
     * 添加订阅
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     */
    void getSubscriptionList();

    /**
     * 判断当前专辑是否已经添加到收藏
     * @param album
     */
    boolean isSub(Album album);

}
