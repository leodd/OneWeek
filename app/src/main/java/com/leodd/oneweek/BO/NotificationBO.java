package com.leodd.oneweek.BO;

import android.content.Context;

import com.leodd.oneweek.Beans.PlanBean;
import com.leodd.oneweek.Utils.DateObject;
import com.leodd.oneweek.Utils.DateUtil;
import com.leodd.oneweek.Utils.TimeObject;

import java.util.List;

/**
 * Created by Leodd
 * on 2016/6/4.
 */
public class NotificationBO {
    private List<PlanBean> mPlanBeanList;
    private int mIndicator;
    private DateObject mToday;
    private PlanBeanBO mPlanBeanBO;

    public NotificationBO(Context context) {
        mPlanBeanBO = new PlanBeanBO(context);
    }

    public void getPlanBeanListOfToday() {
        DateObject date = DateUtil.getToday();
        mToday = date;
        mIndicator = 0;

        mPlanBeanList = mPlanBeanBO.getPlanBeansByDate(date.getYear(), date.getMonth(), date.getDay());
    }

    public void clearPlanBeanList() {
        mPlanBeanList = null;
    }

    public PlanBean getNextPlanBeanByTime(TimeObject time) {
        if (mPlanBeanList == null || !DateUtil.getToday().isEqualTo(mToday)) {
            getPlanBeanListOfToday();
        }

        PlanBean item = null;

        while(mIndicator < mPlanBeanList.size()) {
            item = mPlanBeanList.get(mIndicator);
            if(item.getTime().islargerThan(time) && !item.getTime().isEqualTo(time)) {
                break;
            }
            mIndicator ++;
            item = null;
        }

        return item;
    }

    public PlanBean getCurrentPlanBeanByTime(TimeObject time) {
        PlanBean item = null;

        //initiate planBean list and set the indicator
        getNextPlanBeanByTime(time);

        if(mIndicator > 0) {
            item = mPlanBeanList.get(mIndicator - 1);
        }

        return item;
    }
}
