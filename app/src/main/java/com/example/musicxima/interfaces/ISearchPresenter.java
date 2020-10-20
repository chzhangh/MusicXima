package com.example.musicxima.interfaces;

import com.example.musicxima.base.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchCallBack> {
    /**
     * 进行搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 加载更多的搜索结果
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 推荐相似的关键词
     * @param keyword
     */

    void getRecommendWord(String keyword);

}
