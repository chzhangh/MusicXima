package com.example.musicxima.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;

import androidx.annotation.Nullable;

import com.example.musicxima.utils.Constants;
import com.example.musicxima.utils.LogUtil;

public class XimalayaDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "XimalayaDBHelper";

    public XimalayaDBHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG,"onCreate"+db);
        //创建数据库表
        String subTbSql = "create table "+Constants.SUB_TB_NAME+"(" +
                Constants.SUB_ID+" integer primary key autoincrement," +
                Constants.SUB_COVERURL+" varchar," +
                Constants.SUB_TITLE+" varchar," +
                Constants.SUB_DESCRIPTION+" varchar," +
                Constants.SUB_PLAY_COUNT+" integer," +
                Constants.SUB_TRACKS_COUNT+" integer," +
                Constants.SUB_AUTHOR_NAME+" varchar," +
                Constants.SUB_ALBUM_ID+" integer" +
                ");";
        db.execSQL(subTbSql);

        //创建历史记录表
        String historyTbSql = "create table " + Constants.HISTORY_TB_NAME + "(" +
                Constants.HISTORY_ID + " integer primary key autoincrement, " +
                Constants.HISTORY_TRACK_ID + " integer, " +
                Constants.HISTORY_TITLE + " varchar," +
                Constants.HISTORY_COVER + " varchar," +
                Constants.HISTORY_PLAY_COUNT + " integer," +
                Constants.HISTORY_DURATION + " integer," +
                Constants.HISTORY_AUTHOR + " varchar," +
                Constants.HISTORY_UPDATE_TIME + " integer" +
                ")";
        db.execSQL(historyTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
