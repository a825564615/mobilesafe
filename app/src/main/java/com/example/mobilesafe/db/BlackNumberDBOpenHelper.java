package com.example.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abc on 2016/2/9.
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {
    /**
     * 数据库构建的方法  数据库名称 blacknumber.db
     * @param context
     */
    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement" +
                ",number varchar(20),mode varchar(2))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
