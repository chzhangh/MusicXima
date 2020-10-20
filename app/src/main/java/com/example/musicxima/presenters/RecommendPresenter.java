package com.example.musicxima.presenters;

import com.example.musicxima.DataBase.XimalayaApi;
import com.example.musicxima.interfaces.IRecommendCallback;
import com.example.musicxima.interfaces.IRecommendPresenter;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {
    private static final String TAG = "RecommendPresenter";
    private List<IRecommendCallback> mCallbacks = new ArrayList<>();
    private static RecommendPresenter sInstance = null;
    private List<Album> mCurrentRecommend = null;

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

    /**
     * 获取当前的推荐专辑列表
     * @return
     */

   public  List<Album> getCurrentRecommend(){
        return  mCurrentRecommend;
   }

    /**
     * 接口实现，获取推荐的内容
     *  /**
     *      * 获取推荐内容，其实就是猜你喜欢
     *      * 这个接口，3.10.6 获取猜你喜欢的专辑
     *      *
     *
     */
    @Override
    public void getRecommendList() {
        upLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getInstance();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
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
                handleError();
            }
        });
    }




    private void handleError() {
        //通知ui更新
        if (mCallbacks != null) {
            for (IRecommendCallback mCallback : mCallbacks) {
                mCallback.onNetWorkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
           // albumList.clear();
            if(albumList.size() == 0){
                for (IRecommendCallback mCallback : mCallbacks) {
                    mCallback.onEmpty();
                }
            }else{
                for (IRecommendCallback mCallback : mCallbacks) {
                    mCallback.onRecommendListLoaded(albumList);//获取推荐的结果
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    private void upLoading(){
        for (IRecommendCallback mCallback : mCallbacks) {
            mCallback.onLoading();
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
