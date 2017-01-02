package com.leodd.oneweek.DAO;

import android.content.ContentValues;

import java.util.List;
import java.util.Map;

/**
 * Created by Leodd
 * On 2016/4/17.
 */
public interface IServerDAO {

    /**
     * add values to the database
     *
     * @param table the name of the table
     * @param values value object
     * @return the id of the inserted value
     */
    long add(String table, ContentValues values);

    /**
     * add values to the database
     *
     * @param sql sql command
     * @return whether successfully adding value
     */
    boolean add(String sql);

    /**
     * delete certain value from the database
     *
     * @param table name of the table
     * @param whereClause 条件子句
     * @param selectionArgs 条件自居参数
     * @return whether successfully deleting value
     */
    @Deprecated
    boolean delete(String table, String whereClause, String[] selectionArgs);

    /**
     * delete certain value from the database
     *
     * @param sql sql command
     * @return whether successfully deleting value
     */
    boolean delete(String sql);

    /**
     * query values from the database
     *
     * @param table the name of the table
     * @param columns the name of columns going to be queried
     * @param whereClause 条件子句
     * @param selectionArgs 条件子句参数
     * @param groupBy 分组控制
     * @param having 分组过滤
     * @param orderBy 排序
     * @param limit 分页
     * @return a series of values
     */
    @Deprecated
    List<Map<String, String>> queryMulti(String table,
                                                String[] columns,
                                                String whereClause,
                                                String[] selectionArgs,
                                                String groupBy,
                                                String having,
                                                String orderBy,
                                                String limit);

    /**
     * query values from the database
     *
     * @param sql sql command
     * @return a series of value
     */
    List<Map<String, String>> queryMulti(String sql);

    /**
     * 根据条件查询单个值
     *
     * @param table   要查询的数据所在的表名
     * @param columns 要查询的列名
     * @param key     查询的依据的列名
     * @param value   查询依据值
     * @return 查询结果
     */
    @Deprecated
    String queryValue(String table,
                             String[] columns,
                             String key,
                             String value);

    /**
     * 根据SQL语句查询单个值
     *
     * @param sql SQL语句
     * @return 查询结果
     */
    String queryValue(String sql);

    /**
     * 根据SQl语句新建表
     *
     * @param sql
     * @return
     */
    boolean createTable(String sql);

    /**
     * 清除指定表中所有数据
     *
     * @param table 表的名称
     * @return
     */
    boolean droopTable(String table);

    /**
     * update the data of data base
     * @param string sql command
     * @return return true if succeed
     */
    boolean update(String string);

    /**
     * update the data of certain element
     * @param table the name of table
     * @param id the identification number of the element
     * @param contentValues the content value
     * @return return true if succeed
     */
    boolean update(String table, int id, ContentValues contentValues);
}
