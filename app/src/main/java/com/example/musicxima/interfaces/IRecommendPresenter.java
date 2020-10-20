package com.example.musicxima.interfaces;

import com.example.musicxima.base.IBasePresenter;

/**
 * 主界面主动发起的动作
 */
public interface IRecommendPresenter extends IBasePresenter<IRecommendCallback> {
    //获取到推荐内容
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();
}
