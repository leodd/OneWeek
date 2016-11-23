package com.leodd.oneweek.BO;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.leodd.oneweek.Utils.DateObject;
import com.leodd.oneweek.Utils.DayType;
import com.leodd.oneweek.Utils.TableName;
import com.leodd.oneweek.Utils.TableUtil;
import com.leodd.oneweek.DAO.DAOFactory;
import com.leodd.oneweek.DAO.IServerDAO;

import java.util.List;
import java.util.Map;

/**
 * Created by Leodd
 * On 2016/4/17.
 */
public class ServerBO {
    private IServerDAO serverDAO;

    public ServerBO(Context context) {
        serverDAO = DAOFactory.getInstance(context);
    }

    public Map<String, String> getNormaPlanByID(int id) {
        return null;
    }

    public List<Map<String, String>> getNormalPlansByDate(DateObject date) {
        List<Map<String, String>> list = getNormalPlansByDate(date.toString());
        return list;
    }

    public List<Map<String, String>> getNormalPlansByDate(String dateString) {
        List<Map<String, String>> list;

        list = serverDAO.queryMulti(TableUtil.TABLE_NORMAL_PLAN, TableUtil.QUERY_ALL_COLUMNS_IN_NORMAL_PLAN, "date LIKE ?", new String[] {dateString}, null, null, "_id DESC", null);

        return list;
    }

    public Map<String, String> getRecyclePlanByID(int id) {
        return null;
    }

    protected String dayTypeToString(int dayType) {
        StringBuilder sb = new StringBuilder(7);

        sb.append((dayType & DayType.SUNDAY) !=0 ? "1" : "0");
        sb.append((dayType & DayType.SATURDAY) != 0 ? "1" : "0");
        sb.append((dayType & DayType.FRIDAY) !=0 ? "1" : "0");
        sb.append((dayType & DayType.THURSDAY) !=0 ? "1" : "0");
        sb.append((dayType & DayType.WEDNESDAY) !=0 ? "1" : "0");
        sb.append((dayType & DayType.TUESDAY) !=0 ? "1" : "0");
        sb.append((dayType & DayType.MONDAY) !=0 ? "1" : "0");

        return sb.toString();
    }

    protected int stringToDayType(String dayString) {
        if(dayString.length() != 7) {
            return 0;
        }

        int dayType = 0;

        for(int i = 0; i < 7; i++) {
            dayType <<= 1;
            dayType += dayString.charAt(i) == '1' ? 1 : 0;
        }

        return dayType;
    }

    public List<Map<String, String>> getRecyclePlanByWeek(int dayType) {
        List<Map<String, String>> list;

        StringBuilder dayTypeKey = new StringBuilder(7);

        dayTypeKey.append((dayType & DayType.SUNDAY) !=0 ? "1" : "_");
        dayTypeKey.append((dayType & DayType.SATURDAY) != 0 ? "1" : "_");
        dayTypeKey.append((dayType & DayType.FRIDAY) !=0 ? "1" : "_");
        dayTypeKey.append((dayType & DayType.THURSDAY) !=0 ? "1" : "_");
        dayTypeKey.append((dayType & DayType.WEDNESDAY) !=0 ? "1" : "_");
        dayTypeKey.append((dayType & DayType.TUESDAY) !=0 ? "1" : "_");
        dayTypeKey.append((dayType & DayType.MONDAY) !=0 ? "1" : "_");

        list = serverDAO.queryMulti(TableUtil.TABLE_RECYCLE_PLAN, TableUtil.QUERY_ALL_COLUMNS_IN_RECYCLE_PLAN, "week LIKE ?", new String[] {dayTypeKey.toString()}, null, null, "_id DESC", null);

        return list;
    }

    public long addValueToNormalPlan(ContentValues values) {
        return serverDAO.add(TableName.TABLE_NORMAL_PLAN, values);
    }

    public long addValueToRecyclePlan(ContentValues values) {
        return serverDAO.add(TableName.TABLE_RECYCLE_PLAN, values);
    }

    public void removeValueFromNormalPlan(long id) {
        serverDAO.delete(TableName.TABLE_NORMAL_PLAN, "_id=?", new String[] {String.valueOf(id)});
    }

    public void removeValueFromRecyclePlan(long id) {
        serverDAO.delete(TableName.TABLE_RECYCLE_PLAN, "_id=?", new String[] {String.valueOf(id)});
    }

    public void clearAllDataInDataBase() {
        serverDAO.droopTable(TableUtil.TABLE_NORMAL_PLAN);
        serverDAO.droopTable(TableUtil.TABLE_RECYCLE_PLAN);
        serverDAO.droopTable(TableUtil.TABLE_NOTE);

        serverDAO.createTable(TableUtil.CREATE_TABLE_NORMAL_PLAN);
        serverDAO.createTable(TableUtil.CREATE_TABLE_RECYCLE_PLAN);
        serverDAO.createTable(TableUtil.CREATE_TABLE_NOTE);
    }

}
