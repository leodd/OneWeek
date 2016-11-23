package com.leodd.oneweek.Utils;

/**
 * Created by Leodd
 * on 2016/4/20.
 */
public class TimeObject {
    private int mHour;
    private int mMinute;

    public TimeObject(int hour, int minute) {
        setTime(hour, minute);
    }

    public TimeObject(String timeString) {
        setTime(timeString);
    }

    public void setTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
    }

    public void setTime(String timeString) {
        if (timeString == null) {
            throw new IllegalArgumentException("timeString == null");
        }
        int firstIndex = timeString.indexOf(':');

        if (firstIndex == -1 || firstIndex == 0
                || firstIndex + 1 == timeString.length()) {
            throw new IllegalArgumentException();
        }
        // parse each part of the string
        mHour = Integer.parseInt(timeString.substring(0, firstIndex));
        mMinute = Integer.parseInt(timeString.substring(firstIndex + 1));
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    private static final String PADDING = "00";

    /*
    * Private method to format the time
    */
    private void format(int value, int digits, StringBuilder sb) {
        String str = String.valueOf(value);
        if (digits - str.length() > 0) {
            sb.append(PADDING.substring(0, digits - str.length()));
        }
        sb.append(str);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(5);

        format(mHour, 2, sb);
        sb.append(":");
        format(mMinute, 2, sb);

        return sb.toString();
    }

    public boolean islargerThan(TimeObject time) {
        return mHour > time.getHour() || (mHour == time.getHour() && mMinute >= time.getMinute());
    }

    public boolean isEqualTo(TimeObject time) {
        return (mHour == time.getHour()) && (mMinute == time.getMinute());
    }
}
