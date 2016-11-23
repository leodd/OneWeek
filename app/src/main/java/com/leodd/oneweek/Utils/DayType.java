package com.leodd.oneweek.Utils;

/**
 * Created by Leodd
 * on 2016/4/19.
 */
public class DayType {
    public static final int MONDAY = (1);
    public static final int TUESDAY = (1<<1);
    public static final int WEDNESDAY = (1<<2);
    public static final int THURSDAY = (1<<3);
    public static final int FRIDAY = (1<<4);
    public static final int SATURDAY = (1<<5);
    public static final int SUNDAY = (1<<6);

    public static final int MASK = MONDAY |
            TUESDAY |
            WEDNESDAY |
            THURSDAY |
            FRIDAY |
            SATURDAY |
            SUNDAY;
}
