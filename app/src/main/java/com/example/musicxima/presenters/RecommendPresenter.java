package com.example.musicxima.presenters;

import com.example.musicxima.interfaces.IRecommendCallback;
import com.example.musicxima.interfaces.IRecommendPresenter;
import com.example.musicxima.utils.Constants;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {
    private static final String TAG = "RecommendPresenter";
    private List<IRecommendCallback> mCallbacks = new ArrayList<>();
    private static RecommendPresenter sInstance = null;

    private RecommendPresenter(){

    }

    /**
     * 懒汉式单例
     * @return
     */
    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class){
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }
    @Override
    public void getRecommendList() {
        getRecommendData();

    }

    /**
     * 获取推荐内容，其实就是猜你喜欢
     * 这个接口，3.10.6 获取猜你喜欢的专辑
     *
     */

    private void getRecommendData() {
        //封装参数
        Map<String, String> map = new HashMap<>();
        //这个参数表示一页数据返回多少条数据
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMANND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread name"+Thread.currentThread().getName());
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    /*if (albumList != null) {
                        LogUtil.d(TAG,"size -->" +albumList.size());
                    }*/
                    //数据回来以后，我们就去更新ui
                    //upRecommandUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error   -->" + i);
                LogUtil.d(TAG,"errorMsg"+s);
            }
        });
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知ui更新
        if (mCallbacks != null) {
            for (IRecommendCallback mCallback : mCallbacks) {
                mCallback.onRecommendListLoaded(albumList);//获取推荐的结果
            }
        }
    }


    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendCallback callback) {
        if (mCallbacks!=null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }


}
