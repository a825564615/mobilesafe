package com.example.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.mobilesafe.db.BlackNumberDBOpenHelper;
import com.example.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库的增删改查
 * Created by abc on 2016/2/9.
 */
public class BlackNumberDao  {
    private BlackNumberDBOpenHelper helper ;

    /**
     * 构造方法
     * @param context   上下文
     */
    public BlackNumberDao(Context context){
         helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 查询黑名单号码是否存在
     * @param number
     * @return
     */
    public boolean find(String number){
        boolean result=false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from blacknumber where number=?", new String[]{number});
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 增加黑名单信息
     * @param number 增加的号码
     * @param mode 拦截模式
     */
    public void add(String number ,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number",number);
        values.put("mode",mode);
        db.insert("blacknumber", null, values);
        db.close();
    }

    /**
     * 增加黑名单信息
     * @param number 修改的号码
     * @param mode 修改的拦截模式
     */
    public void update(String number ,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",mode);
        db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
    }

    /**
     * 删除的黑名单信息
     * @param number 删除的的号码
     */
    public void delete(String number){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }

    /**
     * 查找所有的黑名单
     */
    public List<BlackNumberInfo> findAll(){
        List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db  = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber",null);
        while(cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(1));
            info.setMode(cursor.getString(2));
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }
}
