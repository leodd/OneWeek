package com.leodd.oneweek.BO;

import android.content.ContentValues;
import android.content.Context;

import com.leodd.oneweek.DAO.DAOFactory;
import com.leodd.oneweek.DAO.IServerDAO;
import com.leodd.oneweek.Utils.CalendarDate;
import com.leodd.oneweek.Utils.DayOfWeek;
import com.leodd.oneweek.Utils.TableUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by Leodd on 2016/12/17.
 * this class is an implementation of the IServerBO interface
 */
public class ServerBO implements IServerBO {
    private IServerDAO serverDAO;

    public ServerBO(Context context) {
        serverDAO = DAOFactory.getInstance(context);
    }

    @Override
    public List<Map<String, String>> getNormalPlanDataByDate(String string) {
        List<Map<String, String>> res;

        res = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_NORMAL_PLAN +
                " WHERE date LIKE '" + string + "'");

        return res;
    }

    private String dayOfWeekToString(int dayOfWeek) {
        StringBuilder sb = new StringBuilder(7);

        for(int i = 0; i < 7; i++) {
            int mask = 1 << i;
            sb.append((mask & dayOfWeek) != 0 ? '1' : '0');
        }

        return sb.toString();
    }

    @Override
    public List<Map<String, String>> getRecyclePlanDataByDate(String string) {
        List<Map<String, String>> res;

        CalendarDate date = new CalendarDate(string + " 00:00");

        res = getRecyclePlanDataByWeek(1 << date.dayOfWeek());

        return res;
    }

    @Override
    public List<Map<String, String>> getRecyclePlanDataByWeek(int dayOfWeek) {
        List<Map<String, String>> res;

        String string = dayOfWeekToString(dayOfWeek);

        String dayOfWeekStr = string.replace('0', '_');

        res = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_RECYCLE_PLAN +
                " WHERE week LIKE '" + dayOfWeekStr + "'");

        return res;
    }

    @Override
    public List<Map<String, String>> getPlanDataByDate(String string) {
        List<Map<String, String>> res;
        List<Map<String, String>> temp;

        res = getNormalPlanDataByDate(string);
        temp = getRecyclePlanDataByDate(string);

        res.addAll(temp);

        return res;
    }

    @Override
    public Map<String, String> getUpComingPlanData() {
        CalendarDate calendarDate = new CalendarDate();

        String string = dayOfWeekToString(1 << calendarDate.dayOfWeek());
        String dayOfWeek = string.replace('0', '_');

        List<Map<String, String>> temp1;
        List<Map<String, String>> temp2;

        temp1 = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_NORMAL_PLAN +
                " WHERE date LIKE '" + calendarDate.getDateString() + "' AND " +
                "time > time('" + calendarDate.getTimeString() + "') " +
                "ORDER BY time ASC " +
                "LIMIT 1");

        temp2 = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_RECYCLE_PLAN +
                " WHERE week LIKE '" + dayOfWeek + "' AND " +
                "time > time('" + calendarDate.getTimeString() + "') " +
                "ORDER BY time ASC " +
                "LIMIT 1");

        if(temp1.size() != 0 && temp2.size() != 0) {
            String time1 = temp1.get(0).get("time");
            String time2 = temp2.get(0).get("time");

            return time1.compareTo(time2) <= 0 ? temp1.get(0) : temp2.get(0);
        }
        else if (temp1.size() != 0 && temp2.size() == 0) {
            return temp1.get(0);
        }
        else if (temp1.size() == 0 && temp2.size() != 0) {
            return temp2.get(0);
        }

        return null;
    }

    @Override
    public Map<String, String> getCurrentPlanData() {
        CalendarDate calendarDate = new CalendarDate();

        String string = dayOfWeekToString(1 << calendarDate.dayOfWeek());
        String dayOfWeek = string.replace('0', '_');

        List<Map<String, String>> temp1;
        List<Map<String, String>> temp2;

        temp1 = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_NORMAL_PLAN +
                " WHERE date LIKE '" + calendarDate.getDateString() + "' AND " +
                "time <= time('" + calendarDate.getTimeString() + "') " +
                "ORDER BY time DESC " +
                "LIMIT 1");

        temp2 = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_RECYCLE_PLAN +
                " WHERE week LIKE '" + dayOfWeek + "' AND " +
                "time <= time('" + calendarDate.getTimeString() + "') " +
                "ORDER BY time DESC " +
                "LIMIT 1");

        if(temp1.size() != 0 && temp2.size() != 0) {
            String time1 = temp1.get(0).get("time");
            String time2 = temp2.get(0).get("time");

            return time1.compareTo(time2) >= 0 ? temp1.get(0) : temp2.get(0);
        }
        else if (temp1.size() != 0 && temp2.size() == 0) {
            return temp1.get(0);
        }
        else if (temp1.size() == 0 && temp2.size() != 0) {
            return temp2.get(0);
        }

        return null;
    }

    @Override
    public Map<String, String> getNormalPlanDataByID(int id) {
        List<Map<String, String>> temp;

        temp = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_NORMAL_PLAN +
                " WHERE _id = " + id);

        return temp.size() == 0 ? null : temp.get(0);
    }

    @Override
    public Map<String, String> getRecyclePlanDataByID(int id) {
        List<Map<String, String>> temp;

        temp = serverDAO.queryMulti("SELECT * FROM " + TableUtil.TABLE_RECYCLE_PLAN +
                " WHERE _id = " + id);

        return temp.size() == 0 ? null : temp.get(0);
    }

    @Override
    public void updateNormalPlanData(int id, ContentValues contentValues) {
        serverDAO.update(TableUtil.TABLE_NORMAL_PLAN, id, contentValues);
    }

    @Override
    public void updateRecyclePlanData(int id, ContentValues contentValues) {
        serverDAO.update(TableUtil.TABLE_RECYCLE_PLAN, id, contentValues);
    }

    @Override
    public int addNormalPlanData(ContentValues contentValues) {
        return (int)serverDAO.add(TableUtil.TABLE_NORMAL_PLAN, contentValues);
    }

    @Override
    public int addRecyclePlanData(ContentValues contentValues) {
        return (int)serverDAO.add(TableUtil.TABLE_RECYCLE_PLAN, contentValues);
    }

    @Override
    public void removeNormalPlanData(int id) {
        serverDAO.delete("DELETE FROM " + TableUtil.TABLE_NORMAL_PLAN + " WHERE _id = " + id);
    }

    @Override
    public void removeRecyclePlanData(int id) {
        serverDAO.delete("DELETE FROM " + TableUtil.TABLE_RECYCLE_PLAN + " WHERE _id = " + id);
    }

    @Override
    public void clearDataBase() {
        serverDAO.droopTable(TableUtil.TABLE_NORMAL_PLAN);
        serverDAO.droopTable(TableUtil.TABLE_RECYCLE_PLAN);
        serverDAO.droopTable(TableUtil.TABLE_NOTE);

        serverDAO.createTable(TableUtil.CREATE_TABLE_NORMAL_PLAN);
        serverDAO.createTable(TableUtil.CREATE_TABLE_RECYCLE_PLAN);
        serverDAO.createTable(TableUtil.CREATE_TABLE_NOTE);
    }
}
