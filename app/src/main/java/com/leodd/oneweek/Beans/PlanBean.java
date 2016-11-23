package com.leodd.oneweek.Beans;

import com.leodd.oneweek.Utils.DateObject;
import com.leodd.oneweek.Utils.DayType;
import com.leodd.oneweek.Utils.TimeObject;

/**
 * Created by Leodd
 * on 2016/4/19.
 */
public class PlanBean {
    private TimeObject time = new TimeObject(0,0); //time of the plan
    private String content = ""; //the detail of the plan
    private long id = -1;
    private boolean isRecycle = false; //whether the plan is a recycle plan
    private int week = DayType.MONDAY |
            DayType.TUESDAY |
            DayType.WEDNESDAY |
            DayType.THURSDAY |
            DayType.FRIDAY |
            DayType.SATURDAY |
            DayType.SUNDAY; //days that recycle
    private DateObject date = new DateObject(0,0,0);
    private boolean isAlarm = false;
    private boolean isShake = false;
    private boolean isNotify = true;
    private boolean isPass = false; //whether the plan is passed
    private boolean isFocus = false; //whether the plan is focused

    public PlanBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TimeObject getTime() {
        return time;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public String getContent() {
        return content;
    }

    public boolean isRecycle() {
        return isRecycle;
    }

    public int getWeek() {
        return week;
    }

    public DateObject getDate() {
        return date;
    }

    public boolean isAlarm() {
        return isAlarm;
    }

    public boolean isShake() {
        return isShake;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public boolean isPass() {
        return isPass;
    }

    public void setTime(TimeObject time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRecycle(boolean recycle) {
        isRecycle = recycle;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public void setDate(DateObject date) {
        this.date = date;
    }

    public void setAlarm(boolean alarm) {
        isAlarm = alarm;
    }

    public void setShake(boolean shake) {
        isShake = shake;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }
}
