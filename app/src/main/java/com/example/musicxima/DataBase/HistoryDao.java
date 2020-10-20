package com.example.musicxima.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.utils.Constants;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import static com.example.musicxima.utils.Constants.HISTORY_AUTHOR;
import static com.example.musicxima.utils.Constants.HISTORY_COVER;
import static com.example.musicxima.utils.Constants.HISTORY_DURATION;
import static com.example.musicxima.utils.Constants.HISTORY_PLAY_COUNT;
import static com.example.musicxima.utils.Constants.HISTORY_TB_NAME;
import static com.example.musicxima.utils.Constants.HISTORY_TITLE;
import static com.example.musicxima.utils.Constants.HISTORY_UPDATE_TIME;

public class HistoryDao implements IHistroryDao {
    private static final String TAG = "HistoryDao";
    private final XimalayaDBHelper mXimalayaDBHelper;
    private IHisroryCallBack mCallBack = null;
    private Object mLock = new Object();

    public HistoryDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallBack(IHisroryCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void addHistory(Track track) {
        synchronized(mLock) {
            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                //先去删除
                db.beginTransaction();
                ContentValues values = new ContentValues();
                //封装数据、
                values.put(HISTORY_TITLE, track.getTrackTitle());
                values.put(HISTORY_PLAY_COUNT, track.getPlayCount());
                values.put(HISTORY_DURATION, track.getDuration());
                values.put(HISTORY_UPDATE_TIME, track.getUpdatedAt());
                values.put(HISTORY_COVER, track.getCoverUrlLarge());
                values.put(HISTORY_AUTHOR, track.getAnnouncer().getNickname());
                db.insert(Constants.HISTORY_TB_NAME, null, values);
                db.setTransactionSuccessful();
                isSuccess = true;

            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                mCallBack.onHistoryAdd(isSuccess);
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized(mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.beginTransaction();//开启事务
                //插入数据
                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                LogUtil.d(TAG, "delete--" + delete);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;

            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallBack != null) {
                    mCallBack.onHistoryDel(isDeleteSuccess);
                }
            }
        }
    }

    @Override
    public void clearHistory(Track track) {
        synchronized(mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.beginTransaction();//开启事务
                db.delete(HISTORY_TB_NAME, null, null);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;

            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                //通知出去
                if (mCallBack != null) {
                    mCallBack.onHistoryDel(isDeleteSuccess);
                }
            }
        }
    }

    @Override
    public void listHistory() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            List<Track> histories = new ArrayList<>();
            try {
                db = mXimalayaDBHelper.getReadableDatabase();
                db.beginTransaction();
                Cursor cursor = db.query(HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
                while (cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String title = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String cover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlSmall(cover);
                    track.setCoverUrlMiddle(cover);
                    String author = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    histories.add(track);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                //通知出去
                if (mCallBack != null) {
                    mCallBack.onHistoryLoaded(histories);
                }
            }
        }
    }
}
