package com.leodd.oneweek.Utils;

import android.util.Log;

/**
 * Created by Leodd
 * on 2016/4/26.
 */
public class WeekUtil {
    /**
     * 蔡勒公式
     * W=[C/4]-2C+y+[y/4]+[26(m+1)/10]+d-1 （其中[ ]为取整符号）
     *
     * 其中,W是所求日期的星期数.如果求得的数大于7,可以减去7的倍数,
     * 直到余数小于7为止.c是公元年份的前两位数字,
     * y是已知公元年份的后两位数字;m是月数,d是日数.
     * 方括[ ]表示只截取该数的整数部分。
     *
     * 还有一个特别要注意的地方:所求的月份如果是1月或2月,则应视为前一年的13月或14月.
     * 所以公式中m 的取值范围不是1-12,而是3-14.
     */
    public static int getWeekByDate(int year, int month, int day) {
        int W, C, y, m, d;

        y = year;
        m = month;
        d = day;

        if(m == 1 || m == 2) {
            y --;
            m = m == 1 ? 13 : 14;
        }

        C = y / 100;
        y = y % 100;

        W = (C / 4) - (2 * C) + y + (y / 4) + (26 * (m + 1) / 10) + d + 5;

        W = W % 7;

        return (1 << W);
    }

    public static int getWeekByDate(DateObject date) {
        return getWeekByDate(date.getYear(), date.getMonth(), date.getDay());
    }
}
