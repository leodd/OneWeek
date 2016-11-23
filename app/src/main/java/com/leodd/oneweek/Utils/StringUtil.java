package com.leodd.oneweek.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Leodd
 * on 2016/5/9.
 */
public class StringUtil {
    public static List<String> seriesCreater(int startNum, int count) {
        String[] values = new String[count];
        for (int i = startNum; i < startNum + count; i++) {
            String tempValue = (i < 10 ? "0" : "") + i;
            values[i - startNum] = tempValue;
        }
        return Arrays.asList(values);
    }
}
