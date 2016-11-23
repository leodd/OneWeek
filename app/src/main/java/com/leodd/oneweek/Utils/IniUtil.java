package com.leodd.oneweek.Utils;

import android.content.Context;

import com.leodd.oneweek.BO.PlanBeanBO;
import com.leodd.oneweek.Beans.PlanBean;

/**
 * Created by Leodd
 * on 2016/6/9.
 */
public class IniUtil {
    public static void fistTimeInitialize(Context context) {
        ACache aCache = ACache.get(context);

        String mark = aCache.getAsString("first_time_mark");
        if(mark == null) {
            PlanBeanBO planBeanBO = new PlanBeanBO(context);

            PlanBean item = new PlanBean();

            item.setDate(DateUtil.getToday());
            item.setTime(new TimeObject(11, 0));
            item.setContent("这是OneWeek的一个使用引导~\n\n点击下方的【一般计划】按钮可以创建一个普通的计划~\n左右滑动可以切换日期~");
            planBeanBO.addPlanBeanToDataBase(item);

            item.setTime(new TimeObject(12, 0));
            item.setRecycle(true);
            item.setWeek(DayType.MASK);
            item.setContent("快看你发现了什么？！\n这是一个循环计划！\n\n点击下方的【循环计划】按钮创建一个循环计划试试吧~");
            planBeanBO.addPlanBeanToDataBase(item);

            aCache.put("first_time_mark", "mark");
        }
    }
}
