package com.example.musicxima.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * 逻辑层通知界面更新的接口
 */
public interface IRecommendCallback {
    /**
     * 获取推荐内容的结果
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 加载更多
     * @param result
     */
    void onLoadMore(List<Album> result);

    /**
     * 下拉加载刷新更多的结果
     * onRefreshMore
     */
    void onRefreshMore(List<Album> result);
}
