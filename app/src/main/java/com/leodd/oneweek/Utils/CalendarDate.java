package com.leodd.oneweek.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leodd on 2016/12/17.
 * This class simplifies date operation in java.
 */

public class CalendarDate extends Date {

    public CalendarDate() {
        super();
    }

    public CalendarDate(Date date) {
        super();
        setTime(date.getTime());
    }

    public CalendarDate(String timeString) {
        super();
        setTime(timeString);
    }

    public CalendarDate(int year, int month, int day, int hour, int minute) {
        super();
        setTime(year, month, day, hour, minute);
    }

    public void setTime(String timeString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newDate;

        try {
            newDate = dateFormat.parse(timeString);
        }
        catch (Exception e) {
            newDate = new Date();
            e.printStackTrace();
        }

        super.setTime(newDate.getTime());
    }

    public void setTime(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        super.setTime(calendar.getTimeInMillis());
    }

    public int year() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        return calendar.get(Calendar.YEAR);
    }

    public int month() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        return calendar.get(Calendar.MONTH);
    }

    public int day() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int hour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int minute() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        return calendar.get(Calendar.MINUTE);
    }

    public int dayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(this);
    }

    public String getTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(this);
    }

    public String getShortTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(this);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(Date anotherDate) {
        return super.compareTo(anotherDate);
    }
}
