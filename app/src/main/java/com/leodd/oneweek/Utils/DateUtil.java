package com.leodd.oneweek.Utils;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;

/**
 * Created by Leodd
 * on 2016/5/14.
 */
public class DateUtil extends WeekUtil {
    public static DateObject getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new java.util.Date());

        return new DateObject(dateStr);
    }

    public static DateObject getOffsetDate(DateObject date, int offset) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(date.getYear(), date.getMonth() - 1, date.getDay());

        calendar.add(Calendar.DAY_OF_MONTH, offset);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return new DateObject(year, month, day);
    }
}
