package com.example.musicxima.DataBase;

import com.example.musicxima.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {
    private XimalayaApi(){}
    private static XimalayaApi sXimalayaApi;
    public static XimalayaApi getInstance(){
        if (sXimalayaApi==null) {
            synchronized (XimalayaApi.class){
                sXimalayaApi = new XimalayaApi();
            }
        }
        return sXimalayaApi;
    }
    /**
     * 获取推荐列表的内容
     * @param callBack 请求结果的回调接口
     * @param
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack){
        //封装参数
        Map<String, String> map = new HashMap<>();
        //这个参数表示一页数据返回多少条数据
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMANND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }

    /**
     * 根据专辑的id获取到专辑详情列表
     * @param callBack
     * @param currentAlbumId
     * @param currentPageIndex
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack,long currentAlbumId,int currentPageIndex){
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, currentAlbumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, currentPageIndex+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_PAGE_DEFAULT+"");
        CommonRequest.getTracks(map,callBack);
    }

    /**
     * 根据关键字进行搜搜
     * @param keyword
     */
    public void searchByKeyWord(String keyword,int page,IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_PAGE_DEFAULT+"");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    /**
     * 获取搜索专辑热词
     * @param callback
     */
    public void getHotWord(IDataCallBack<HotWordList> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, String.valueOf(Constants.CUONT_HOT_WORD));
        CommonRequest.getHotWords(map, callback);
    }

    /**
     * 获取搜索关键词的联想词
     * @param keyword
     * @param callback
     */

    public void getSuggestWord(String keyword,IDataCallBack<SuggestWords> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callback);
    }
}
