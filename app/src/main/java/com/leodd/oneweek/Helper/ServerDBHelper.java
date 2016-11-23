package com.leodd.oneweek.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.leodd.oneweek.Utils.TableUtil;

/**
 * Created by Leodd
 * On 2016/4/16.
 */
public class ServerDBHelper extends SQLiteOpenHelper {
    private static final int DBVersion = 1;

    public ServerDBHelper(Context context) {
        super(context, TableUtil.DBName, null, DBVersion);
    }

    public ServerDBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableUtil.CREATE_TABLE_NORMAL_PLAN);
        db.execSQL(TableUtil.CREATE_TABLE_RECYCLE_PLAN);
        db.execSQL(TableUtil.CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
