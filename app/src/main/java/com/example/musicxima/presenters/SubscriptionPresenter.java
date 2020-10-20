package com.example.musicxima.presenters;

import com.example.musicxima.DataBase.ISubDaoCallBack;
import com.example.musicxima.DataBase.SubscriptionDao;
import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.interfaces.ISubscriptionCallback;
import com.example.musicxima.interfaces.ISubscriptionPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.example.musicxima.utils.Constants.MAX_SUB_CONT;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallBack {

    private final SubscriptionDao mSubscriptionDao;
    private Map<Long,Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();
    private SubscriptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallBack(this);
    }

    private void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //只调用，不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
    private static  SubscriptionPresenter sSubscriptionPresenter = null;
    public static  ISubscriptionPresenter getInstance(){
        if (sSubscriptionPresenter == null) {
            synchronized (SubscriptionPresenter.class){
                sSubscriptionPresenter = new SubscriptionPresenter();

            }
        }
        return  sSubscriptionPresenter;
    }
    @Override
    public void addSubscription(final Album album) {
        //判断当前的订阅数量不能超过100个
        if (mData.size() >= MAX_SUB_CONT) {
            //给出提示
            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubTooMany();
            }
            return;
        }
      Observable.create(new ObservableOnSubscribe<Object>() {
          @Override
          public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
              if (mSubscriptionDao != null) {
                  mSubscriptionDao.addAlbum(album);
              }
          }
      }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        //判断是否已经加入订阅
        Album result = mData.get(album.getId());
        return result != null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback m) {
        if (!mCallbacks.contains(m)) {
            mCallbacks.add(m);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback m) {
      mCallbacks.remove(m);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscriptions();
        //添加结果回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        listSubscriptions();
        //添加结果回调
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });

    }

    @Override
    public void onSubListLoaded(final List<Album> list) {
        mData.clear();
        for (Album album : list) {
            mData.put(album.getId(),album);
        }
        //通知ui更新
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscriptionLoaded(list);
                }
            }
        });

    }
}
