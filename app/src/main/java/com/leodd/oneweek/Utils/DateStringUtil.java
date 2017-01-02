package com.leodd.oneweek.Utils;

/**
 * Created by leodd on 2016/12/31.
 */

public class DateStringUtil {
    public static String getDateString(CalendarDate date) {
        String res = "";

//        switch (date.dayOfWeek()) {
//            case 0:res += "SUNDAY, ";
//                break;
//            case 1:res += "MONDAY, ";
//                break;
//            case 2:res += "TUESDAY, ";
//                break;
//            case 3:res += "WEDNESDAY, ";
//                break;
//            case 4:res += "THURSDAY, ";
//                break;
//            case 5:res += "FRIDAY, ";
//                break;
//            case 6:res += "SATURDAY, ";
//                break;
//        }

        switch (date.month()) {
            case 0:res += "JANUARY ";
                break;
            case 1:res += "FEBRUARY ";
                break;
            case 2:res += "MARCH ";
                break;
            case 3:res += "APRIL ";
                break;
            case 4:res += "MAY ";
                break;
            case 5:res += "JUNE ";
                break;
            case 6:res += "JULY ";
                break;
            case 7:res += "AUGUST ";
                break;
            case 8:res += "SEPTEMBER ";
                break;
            case 9:res += "OCTOBER ";
                break;
            case 10:res += "NOVEMBER ";
                break;
            case 11:res += "DECEMBER ";
                break;
        }

        res += date.day();

        return res;
    }
}
