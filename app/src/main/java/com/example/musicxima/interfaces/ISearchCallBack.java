package com.example.musicxima.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallBack {
    /**
     * 搜索结果的回调方法
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的接口回调方法
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     * @param result
     * @param isOkay
     */
    void  onLoadMoreResult(List<Album> result,boolean isOkay);

    /**
     * 加载相似的关键词
     * @param keywordList
     */
    void onRecommendWordLoaded(List<QueryResult> keywordList);

    /**
     * 错误通知
     * @param errorCode
     * @param errorMessage
     */
    void onError(int errorCode,String errorMessage);
}
