package com.example.musicxima.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicxima.base.BaseApplication;
import com.example.musicxima.utils.Constants;
import com.example.musicxima.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubDao {
    private static final String TAG = "SubscriptionDao";
    private final XimalayaDBHelper mXimalayaDBHelper;
    private ISubDaoCallBack mCallBack;

    private SubscriptionDao(){
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }
    private static final SubscriptionDao sSubInstance = new SubscriptionDao();

    public static SubscriptionDao getInstance(){
        return  sSubInstance;
    }

    @Override
    public void setCallBack(ISubDaoCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isAddSuccess = false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();//开启事务
            ContentValues values = new ContentValues();
            //封装数据
            values.put(Constants.SUB_COVERURL,album.getCoverUrlLarge());
            values.put(Constants.SUB_TITLE,album.getAlbumTitle());
            values.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            values.put(Constants.SUB_PLAY_COUNT,album.getPlayCount());
            values.put(Constants.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            values.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            values.put(Constants.SUB_ALBUM_ID,album.getId());
            //插入数据
            db.insert(Constants.SUB_TB_NAME,null,values);
            db.setTransactionSuccessful();
            //通知ui
            isAddSuccess = true;
        }catch (Exception e){
            e.printStackTrace();
            if (mCallBack != null) {
                mCallBack.onAddResult(false);
            }
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallBack != null) {
                mCallBack.onAddResult(isAddSuccess);
            }

        }
    }

    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isDeleteSuccess = false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();//开启事务
            //插入数据
            int delete = db.delete(Constants.SUB_TB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtil.d(TAG,"delete--"+delete);
            db.setTransactionSuccessful();
            isDeleteSuccess = true;

        }catch (Exception e){
            e.printStackTrace();
            if (mCallBack != null) {
                mCallBack.onDelResult(false);
            }
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallBack != null) {
                mCallBack.onDelResult(isDeleteSuccess);
            }
        }

    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mXimalayaDBHelper.getReadableDatabase();
            db.beginTransaction();//开启事务
            Cursor query = db.query(Constants.SUB_TB_NAME, null, null, null, null, null, "id desc");
            while (query.moveToNext()) {
                Album album = new Album();
                //图片
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVERURL));
                String subTitle = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                String subDescription = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                int subPlayCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                int subTracksCount = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                String subAuthorname = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                int subAlbumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setCoverUrlLarge(coverUrl);
                album.setAlbumTitle(subTitle);
                album.setAlbumIntro(subDescription);
                album.setPlayCount(subPlayCount);
                album.setIncludeTrackCount(subTracksCount);
                Announcer announcer = new Announcer();
                announcer.setNickname(subAuthorname);
                album.setAnnouncer(announcer);
                album.setId(subAlbumId);
                result.add(album);
            }
            //把数据通知出去
            query.close();
            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();

        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallBack != null) {
                mCallBack.onSubListLoaded(result);
            }
        }


    }
}
