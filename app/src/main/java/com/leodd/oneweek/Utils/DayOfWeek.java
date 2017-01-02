package com.leodd.oneweek.Utils;

/**
 * Created by Leodd
 * on 2016/4/19.
 */
public class DayOfWeek {
    public static final int SUN = (1);
    public static final int MON = (1<<1);
    public static final int TUE = (1<<2);
    public static final int WED = (1<<3);
    public static final int THU = (1<<4);
    public static final int FRI = (1<<5);
    public static final int SAT = (1<<6);

    public static final int ALL = SUN | MON | TUE | WED | THU | FRI | SAT;

    public static final int WORKDAY = MON | TUE | WED | THU | FRI;
}
