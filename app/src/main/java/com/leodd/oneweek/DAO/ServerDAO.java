package com.leodd.oneweek.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leodd.oneweek.Helper.ServerDBHelper;
import com.leodd.oneweek.Utils.TableUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leodd
 * On 2016/4/17.
 */
public class ServerDAO implements IServerDAO {
    private ServerDBHelper dbHelper;

    public ServerDAO(Context context) {
        dbHelper = new ServerDBHelper(context);
    }

    @Override
    public long add(String table, ContentValues values) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = dbHelper.getWritableDatabase();
            id = db.insert(table, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return id;
    }

    @Override
    public boolean add(String sql) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return true;
    }

    @Override
    public boolean delete(String table, String whereClause, String[] selectionArgs) {
        boolean isSuccess = false;
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            long id = db.delete(table, whereClause, selectionArgs);
            isSuccess = id != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return isSuccess;
    }

    @Override
    public boolean delete(String sql) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return true;
    }

    @Override
    public List<Map<String, String>> queryMulti(String table,
                                                String[] columns,
                                                String whereClause,
                                                String[] selectionArgs,
                                                String groupBy,
                                                String having,
                                                String orderBy,
                                                String limit) {
        List<Map<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor;

        try {
            db = dbHelper.getWritableDatabase();
            cursor = db.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<>();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    String columnValue = cursor.getString(i);
                    map.put(columnName, columnValue);
                }

                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return list;
    }

    @Override
    public List<Map<String, String>> queryMulti(String sql) {
        List<Map<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor;

        try {
            db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<>();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    String columnValue = cursor.getString(i);
                    map.put(columnName, columnValue);
                }

                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return list;
    }


    @Override
    public String queryValue(String table, String[] columns, String key, String value) {
        String result = null;
        SQLiteDatabase db = null;
        Cursor cursor;

        try {
            db = dbHelper.getWritableDatabase();
            cursor = db.query(table, columns, key + " like ?", new String[]{value}, null, null, null, null);

            if (cursor.moveToFirst()) {
                String columnName = cursor.getColumnName(0);
                result = cursor.getString(cursor.getColumnIndex(columnName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return result;
    }

    @Override
    public String queryValue(String sql) {
        String result = null;
        SQLiteDatabase db = null;
        Cursor cursor;

        try {
            db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                String columnName = cursor.getColumnName(0);
                result = cursor.getString(cursor.getColumnIndex(columnName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return result;
    }

    @Override
    public boolean createTable(String sql) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return true;
    }

    @Override
    public boolean droopTable(String table) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            db.execSQL("DROP TABLE " + table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return true;
    }
}
