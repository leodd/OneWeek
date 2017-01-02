package com.leodd.oneweek.Models;

import com.leodd.oneweek.Utils.CalendarDate;
import com.leodd.oneweek.Utils.DayOfWeek;

/**
 * Created by Leodd on 2016/12/18.
 * the model of the plan, including recycle plan and normal plan
 */
public class Plan implements Comparable<Plan> {
    private String content; //the detail of the plan
    private int id;
    private boolean isRecycle; //whether the plan is a recycle plan
    private int dayOfWeek;//days that recycle
    private CalendarDate calendarDate;
    private boolean isAlarm;
    private boolean isShake;
    private boolean isNotify;

    public Plan() {
        init();
    }

    private void init() {
        content = "";
        id = -1;
        isRecycle = true;
        dayOfWeek = DayOfWeek.ALL;
        calendarDate = new CalendarDate();
        isAlarm = false;
        isShake = false;
        isNotify = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(CalendarDate calendarDate) {
        this.calendarDate = calendarDate;
    }

    public CalendarDate getDate() {
        return calendarDate;
    }

    public String getContent() {
        return content;
    }

    public boolean isRecycle() {
        return isRecycle;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
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

    public void setContent(String content) {
        this.content = content;
    }

    public void setRecycle(boolean recycle) {
        isRecycle = recycle;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    @Override
    public int compareTo(Plan plan) {
        if(calendarDate.hour() > plan.calendarDate.hour()) {
            return 1;
        }
        else if(calendarDate.hour() < plan.calendarDate.hour()) {
            return -1;
        }
        else {
            if(calendarDate.minute() > plan.calendarDate.minute()) {
                return 1;
            }
            else if(calendarDate.minute() < plan.calendarDate.minute()) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }
}
