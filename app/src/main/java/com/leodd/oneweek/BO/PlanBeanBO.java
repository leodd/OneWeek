package com.leodd.oneweek.BO;

import android.content.ContentValues;
import android.content.Context;

import com.leodd.oneweek.Beans.PlanBean;
import com.leodd.oneweek.Utils.DateObject;
import com.leodd.oneweek.Utils.TimeObject;
import com.leodd.oneweek.Utils.WeekUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Leodd
 * on 2016/4/23.
 */
public class PlanBeanBO extends ServerBO{
    public PlanBeanBO(Context context) {
        super(context);
    }

    public List<PlanBean> getPlanBeansByDate(int year, int month, int day) {
        List<PlanBean> list = new ArrayList<>();

        List<Map<String, String>> normalPlanData = getNormalPlansByDate(new DateObject(year, month, day));

        for(int i = 0; i < normalPlanData.size(); i++) {
            PlanBean planBean = new PlanBean();

            String idString = normalPlanData.get(i).get("_id");
            planBean.setId(Integer.parseInt(idString));

            String timeString = normalPlanData.get(i).get("time");
            planBean.setTime(new TimeObject(timeString));

            String content = normalPlanData.get(i).get("content");
            planBean.setContent(content);

            String isAlarmString = normalPlanData.get(i).get("isAlarm");
            planBean.setAlarm(isAlarmString.equals("1"));

            String dateString = normalPlanData.get(i).get("date");
            planBean.setDate(new DateObject(dateString));

            planBean.setRecycle(false);

            list.add(planBean);
        }

        int week = WeekUtil.getWeekByDate(year, month, day);

        List<Map<String, String>> recyclePlanData = getRecyclePlanByWeek(week);

        for(int i = 0; i < recyclePlanData.size(); i++) {
            PlanBean planBean = new PlanBean();

            String idString = recyclePlanData.get(i).get("_id");
            planBean.setId(Integer.parseInt(idString));

            String timeString = recyclePlanData.get(i).get("time");
            planBean.setTime(new TimeObject(timeString));

            String content = recyclePlanData.get(i).get("content");
            planBean.setContent(content);

            String isAlarmString = recyclePlanData.get(i).get("isAlarm");
            planBean.setAlarm(isAlarmString.equals("1"));

            String recycleString = recyclePlanData.get(i).get("week");
            planBean.setWeek(stringToDayType(recycleString));

            planBean.setRecycle(true);

            list.add(planBean);
        }

        rankByTime(list);

        return list;
    }

    public List<PlanBean> getNormalPlanBeansByDate(int year, int month, int day) {
        List<PlanBean> list = new ArrayList<>();

        List<Map<String, String>> data = getNormalPlansByDate(new DateObject(year, month, day));

        for(int i = 0; i < data.size(); i++) {
            PlanBean planBean = new PlanBean();

            String idString = data.get(i).get("_id");
            planBean.setId(Integer.parseInt(idString));

            String timeString = data.get(i).get("time");
            planBean.setTime(new TimeObject(timeString));

            String content = data.get(i).get("content");
            planBean.setContent(content);

            String isAlarmString = data.get(i).get("isAlarm");
            planBean.setAlarm(isAlarmString.equals("1"));

            String dateString = data.get(i).get("date");
            planBean.setDate(new DateObject(dateString));

            planBean.setRecycle(false);

            list.add(planBean);
        }

        return list;
    }

    public List<PlanBean> getRecyclePlanBeansByWeek(int dayType) {
        List<PlanBean> list = new ArrayList<>();

        List<Map<String, String>> data = getRecyclePlanByWeek(dayType);

        for(int i = 0; i < data.size(); i++) {
            PlanBean planBean = new PlanBean();

            String idString = data.get(i).get("_id");
            planBean.setId(Integer.parseInt(idString));

            String timeString = data.get(i).get("time");
            planBean.setTime(new TimeObject(timeString));

            String content = data.get(i).get("content");
            planBean.setContent(content);

            String isAlarmString = data.get(i).get("isAlarm");
        planBean.setAlarm(isAlarmString.equals("1"));

        String recycleString = data.get(i).get("week");
        planBean.setWeek(stringToDayType(recycleString));

        planBean.setRecycle(true);

        list.add(planBean);
    }

    return list;
}

    public long addPlanBeanToDataBase(PlanBean item) {
        ContentValues cv = new ContentValues();

        cv.put("content", item.getContent());
        cv.put("time", item.getTime().toString());
        cv.put("isAlarm", item.isAlarm());
        cv.put("isNotify", item.isNotify());
        cv.put("isShake", item.isShake());

        if(item.isRecycle()) {
            String weekStr = dayTypeToString(item.getWeek());
            cv.put("week", weekStr);

            return addValueToRecyclePlan(cv);
        }
        else {
            cv.put("date", item.getDate().toString());

            return addValueToNormalPlan(cv);
        }
    }

    public void removePlanBeanFromDataBase(PlanBean item) {
        if(item.isRecycle()) {
            removeValueFromRecyclePlan(item.getId());
        }
        else {
            removeValueFromNormalPlan(item.getId());
        }
    }

    private void rankByTime(List<PlanBean> list) {
        int iterationTimes = list.size();
        PlanBean temp;

        for(int i = 0; i < iterationTimes; i++) {
            for(int k = 0; k < iterationTimes - i - 1; k++) {
                if(list.get(k).getTime().islargerThan(list.get(k+1).getTime())) {
                    temp = list.get(k);
                    list.set(k, list.get(k+1));
                    list.set(k+1, temp);
                }
            }
        }
    }

    public int insertByTime(PlanBean item, List<PlanBean> list) {
        list.add(item);

        int position = 0;
        PlanBean temp;

        for(int i = list.size() - 1; i > 0; i--) {
            if(list.get(i-1).getTime().islargerThan(list.get(i).getTime())) {
                temp = list.get(i);
                list.set(i, list.get(i-1));
                list.set(i-1, temp);
            }
            else {
                position = i;
                break;
            }
        }

        return  position;
    }
}
