package com.leodd.oneweek.Utils;

/**
 * Created by Leodd
 * on 2016/4/22.
 */
public class TableUtil extends TableName {
    public static final String DBName = "OneWeekSever.db";

    public static final String[] QUERY_ALL_COLUMNS_IN_NORMAL_PLAN = {"_id","content","date","time","isAlarm","isNotify","isShake"};
    public static final String[] QUERY_ALL_COLUMNS_IN_RECYCLE_PLAN = {"_id","content","week","time","isAlarm","isNotify","isShake"};
    public static final String[] QUERY_ALL_COLUMNS_IN_NOTE = {"_id","content","date"};

    public static final String CREATE_TABLE_NORMAL_PLAN = "CREATE TABLE normal_plan(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "content TEXT," +
            "date TEXT," +
            "time TEXT," +
            "isAlarm INTEGER," +
            "isNotify INTEGER," +
            "isShake INTEGER" +
            ")";
    public static final String CREATE_TABLE_RECYCLE_PLAN = "CREATE TABLE recycle_plan(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "content TEXT," +
            "week TEXT," +
            "time TEXT," +
            "isAlarm INTEGER," +
            "isNotify INTEGER," +
            "isShake INTEGER" +
            ")";
    public static final String CREATE_TABLE_NOTE = "CREATE TABLE note(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "content TEXT," +
            "date TEXT" +
            ")";
}
