package com.leodd.oneweek.Utils;

/**
 * Created by Leodd
 * on 2016/4/20.
 */
public class DateObject {
    private int mYear;
    private int mMonth;
    private int mDay;

    public DateObject(int year, int month, int day) {
        setDate(year, month, day);
    }

    public DateObject(String timeString) {
        setDate(timeString);
    }

    public void setDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    public void setDate(String timeString) {
        if (timeString == null) {
            throw new IllegalArgumentException("timeString == null");
        }
        int firstIndex = timeString.indexOf('-');
        int secondIndex = timeString.indexOf('-', firstIndex + 1);
        // secondIndex == -1 means none or only one separator '-' has been
        // found.
        // The string is separated into three parts by two separator characters,
        // if the first or the third part is null string, we should throw
        // IllegalArgumentException to follow RI
        if (secondIndex == -1 || firstIndex == 0
                || secondIndex + 1 == timeString.length()) {
            throw new IllegalArgumentException();
        }
        // parse each part of the string
        mYear = Integer.parseInt(timeString.substring(0, firstIndex));
        mMonth = Integer.parseInt(timeString.substring(firstIndex + 1,
                secondIndex));
        mDay = Integer.parseInt(timeString.substring(secondIndex + 1,
                timeString.length()));
    }

    public void setYear(int year) {
        mYear = year;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getDay() {
        return mDay;
    }

    private static final String PADDING = "0000";

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
        StringBuilder sb = new StringBuilder(10);

        format(mYear, 4, sb);
        sb.append("-");
        format(mMonth, 2, sb);
        sb.append("-");
        format(mDay, 2, sb);

        return sb.toString();
    }

    public boolean isEqualTo(DateObject date) {
        return (mYear == date.getYear()) && (mMonth == date.getMonth()) && (mDay == date.getDay());
    }
}
