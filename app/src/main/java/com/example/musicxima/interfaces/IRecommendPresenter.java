package com.example.musicxima.interfaces;

/**
 * 主界面主动发起的动作
 */
public interface IRecommendPresenter {
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

    /**
     * 用于注册ui的回调
     * @param callback
     */
    void registerViewCallback(IRecommendCallback callback);

    /**
     * 反注册UI的回调
     * @param callback
     */
    void unRegisterViewCallback(IRecommendCallback callback);

}
