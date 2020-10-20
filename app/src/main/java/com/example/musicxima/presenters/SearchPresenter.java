package com.example.musicxima.presenters;

import android.util.Log;

import com.example.musicxima.DataBase.XimalayaApi;
import com.example.musicxima.interfaces.ISearchCallBack;
import com.example.musicxima.interfaces.ISearchPresenter;
import com.example.musicxima.utils.Constants;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private List<Album> searchResult = new ArrayList<>();
    private static final String TAG = "SearchPresenter";
    private List<ISearchCallBack> mCallBacks = new ArrayList<>();
    private String mCurrentKeyWord = null; //当前的搜索关键字
    private XimalayaApi mXimalayaApi;
    private static  final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter(){ mXimalayaApi = XimalayaApi.getInstance();}
    private static SearchPresenter sSearchPresenter;

    public static SearchPresenter getInstance(){
        if (sSearchPresenter==null) {
            synchronized (SearchPresenter.class){
                sSearchPresenter = new SearchPresenter();
            }
        }
        return sSearchPresenter;
    }



    @Override
    public void doSearch(String keyword) {
        mCurrentPage = DEFAULT_PAGE;
        searchResult.clear();
        //当网络不好的时候，用于重新搜索，用户会点击搜索
        this.mCurrentKeyWord = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyWord(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                searchResult.addAll(albums);
                if (albums != null) {
                    LogUtil.d(TAG,"album size -->"+ albums.size());
                    if (isLoadedMore) {
                        for (ISearchCallBack callBack : mCallBacks) {
                            if(albums.size()==0){
                                callBack.onLoadMoreResult(searchResult,false);
                            }else{
                                callBack.onLoadMoreResult(searchResult,true);
                            }
                        }
                        isLoadedMore = false;

                    }else{
                        for (ISearchCallBack callBack : mCallBacks) {
                            callBack.onSearchResultLoaded(searchResult);
                        }
                    }

                }else{
                    LogUtil.d(TAG,"album size is null...");
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
              LogUtil.d(TAG,"errcode -->"+errorCode);
              LogUtil.d(TAG,"errormessgae -->"+errorMessage);

                    for (ISearchCallBack callBack : mCallBacks) {
                        callBack.onError(errorCode,errorMessage);
                        if (isLoadedMore) {
                            callBack.onLoadMoreResult(searchResult,false);
                            mCurrentPage--;
                            isLoadedMore = false;
                        }else {
                            callBack.onError(errorCode,errorMessage);
                        }
                    }


            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyWord);
    }

    private boolean isLoadedMore = false;
    @Override
    public void loadMore() {
        //判断有没有必要进行加载更多
        if (searchResult.size()< Constants.COUNT_PAGE_DEFAULT) {
            for (ISearchCallBack callBack : mCallBacks) {
                callBack.onLoadMoreResult(searchResult,false);

            }
        }else{
            isLoadedMore = true;
            mCurrentPage++;
            search(mCurrentKeyWord);
        }

    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWord(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG,"hotword size->."+hotWords.size());
                    for (ISearchCallBack callBack : mCallBacks) {
                        callBack.onHotWordLoaded(hotWords);//回调到ui层
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                LogUtil.d(TAG,"errcode -->"+errorCode);
                LogUtil.d(TAG,"errormessgae -->"+errorMessage);
                for (ISearchCallBack callBack : mCallBacks) {
                    callBack.onError(errorCode,errorMessage);

                }
            }
        });

    }

    @Override
    public void getRecommendWord(final String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    Log.d(TAG,"keywordlist-->"+ keyWordList.size());
                    for (ISearchCallBack callBack : mCallBacks) {
                        callBack.onRecommendWordLoaded(keyWordList);
                    }

                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                LogUtil.d(TAG,"errcode -->"+errorCode);
                LogUtil.d(TAG,"errormessgae -->"+errorMessage);
                for (ISearchCallBack callBack : mCallBacks) {
                    callBack.onError(errorCode,errorMessage);

                }
            }
        });

    }

    @Override
    public void registerViewCallback(ISearchCallBack m) {
        if (!mCallBacks.contains(m)) {
            mCallBacks.add(m);
        }

    }

    @Override
    public void unRegisterViewCallback(ISearchCallBack m) {
         mCallBacks.remove(m);
    }
}
