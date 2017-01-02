//package com.leodd.oneweek.BO;
//
//import android.content.Context;
//
//import com.leodd.oneweek.Models.Plan;
//
//import java.util.List;
//
///**
// * Created by Leodd
// * on 2016/6/4.
// */
//public class NotificationBO {
//    private List<Plan> mPlanList;
//    private int mIndicator;
//    private DateObject mToday;
//    private PlanBO mPlanBO;
//
//    public NotificationBO(Context context) {
//        mPlanBO = new PlanBO(context);
//    }
//
//    public void getPlanBeanListOfToday() {
//        DateObject date = DateUtil.getToday();
//        mToday = date;
//        mIndicator = 0;
//
//        mPlanList = mPlanBO.getPlanBeansByDate(date.getYear(), date.getMonth(), date.getDay());
//    }
//
//    public void clearPlanBeanList() {
//        mPlanList = null;
//    }
//
//    public Plan getNextPlanBeanByTime(TimeObject time) {
//        if (mPlanList == null || !DateUtil.getToday().isEqualTo(mToday)) {
//            getPlanBeanListOfToday();
//        }
//
//        Plan item = null;
//
//        while(mIndicator < mPlanList.size()) {
//            item = mPlanList.get(mIndicator);
//            if(item.getTime().islargerThan(time) && !item.getTime().isEqualTo(time)) {
//                break;
//            }
//            mIndicator ++;
//            item = null;
//        }
//
//        return item;
//    }
//
//    public Plan getCurrentPlanBeanByTime(TimeObject time) {
//        Plan item = null;
//
//        //initiate planBean list and set the indicator
//        getNextPlanBeanByTime(time);
//
//        if(mIndicator > 0) {
//            item = mPlanList.get(mIndicator - 1);
//        }
//
//        return item;
//    }
//}
