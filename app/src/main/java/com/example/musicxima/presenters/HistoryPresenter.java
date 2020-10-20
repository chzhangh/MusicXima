package com.example.musicxima.presenters;

import com.example.musicxima.DataBase.HistoryDao;
import com.example.musicxima.DataBase.IHisroryCallBack;
import com.example.musicxima.DataBase.IHistroryDao;
import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.interfaces.IHistoryCallBack;
import com.example.musicxima.interfaces.IHistoryPresenter;
import com.example.musicxima.utils.Constants;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 历史数量最多100条
 * 如归超过，那么就删除前面添加的，在把当前的历史添加进去，这个
 */
public class HistoryPresenter implements IHistoryPresenter, IHisroryCallBack {
    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallBack> mIHistoryCallBacks = new ArrayList<>();

    private IHistroryDao mHistoryDao;
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;

    private HistoryPresenter() {
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallBack(this);
    }
    private static HistoryPresenter sHistoryPresenter = null;
    public static HistoryPresenter getInstance(){
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class){
                sHistoryPresenter = new HistoryPresenter();
            }
        }
        return sHistoryPresenter;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isOutOfSize = false;//是否超过限定的值
    @Override
    public void addHistory(final Track track) {
        //这个时候需要去判断是否已经超过了100条
        if (mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            isOutOfSize = true;
            //线不能添加，先删除最前面得一条，再添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size()-1));
            this.mCurrentAddTrack = track;
        }else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallBack m) {
        //ui注册过来的
        if (!mIHistoryCallBacks.contains(m)) {
            mIHistoryCallBacks.add(m);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallBack m) {
      //删除ui的注册
        mIHistoryCallBacks.remove(m);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        if (isOutOfSize && mCurrentAddTrack !=null) {
            isOutOfSize = false;
            //添加当前的数据进数据库中
            addHistory(mCurrentAddTrack);
        }else {
            listHistories();
        }

    }

    @Override
    public void onHistoryLoaded(final List<Track> trackList) {
        this.mCurrentHistories = trackList;
        LogUtil.d(TAG,"history--->Size" +trackList.size());
         //去更新ui更新数据
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallBack iHistoryCallBack : mIHistoryCallBacks) {
                    iHistoryCallBack.onHistoryLoaded(trackList);
                }
            }
        });
    }

    @Override
    public void onHistoryClean(boolean isSuccess) {
        listHistories();
    }
}
