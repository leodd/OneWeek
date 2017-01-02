package com.leodd.oneweek.BO;

import android.content.ContentValues;
import android.content.Context;

import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.Utils.CalendarDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Leodd on 2016/12/20.
 * this class implements the server business methods in plan level
 */
public class PlanBO extends ServerBO implements IPlanBO {

    public PlanBO(Context context) {
        super(context);
    }

    private Plan createPlan(Map<String, String> map, String dateString) {
        if(map == null) {
            return null;
        }

        Plan plan = new Plan();

        if(map.containsKey("_id"))
            plan.setId(Integer.parseInt(map.get("_id")));
        if(map.containsKey("content"))
            plan.setContent(map.get("content"));
        if(map.containsKey("date") && map.containsKey("time")) {
            plan.setDate(new CalendarDate(map.get("date") + " " + map.get("time")));
            plan.setDayOfWeek(0);
            plan.setRecycle(false);
        }
        if(map.containsKey("week") && map.containsKey("time")) {
            plan.setDayOfWeek(stringToDayOfWeek(map.get("week")));
            plan.setDate(new CalendarDate(dateString + " " + map.get("time")));
            plan.setRecycle(true);
        }
        if(map.containsKey("isAlarm"))
            plan.setAlarm(map.get("isAlarm").equals("1"));
        if(map.containsKey("isNotify"))
            plan.setNotify(map.get("isNotify").equals("1"));
        if(map.containsKey("isShake"))
            plan.setShake(map.get("isShake").equals("1"));

        return plan;
    }

    private int stringToDayOfWeek(String string) {
        if(string.length() != 7) {
            return 0;
        }

        int dayOfWeek = 0;

        for(int i = 6; i >= 0; i--) {
            dayOfWeek = dayOfWeek << 1;
            dayOfWeek += string.charAt(i) == '1' ? 1 : 0;
        }

        return dayOfWeek;
    }

    @Override
    public List<Plan> getPlanByDate(CalendarDate calendarDate) {
        List<Map<String, String>> temp;

        temp = getPlanDataByDate(calendarDate.getDateString());

        List<Plan> res = new ArrayList<>(temp.size());

        String dateString = calendarDate.getDateString();

        for(Map<String, String> element : temp) {
            res.add(createPlan(element, dateString));
        }

        Collections.sort(res);

        return res;
    }

    @Override
    public List<Plan> getNormalPlanByDate(CalendarDate calendarDate) {
        List<Map<String, String>> temp;

        temp = getNormalPlanDataByDate(calendarDate.getDateString());

        List<Plan> res = new ArrayList<>(temp.size());

        String dateString = calendarDate.getDateString();

        for(Map<String, String> element : temp) {
            res.add(createPlan(element, dateString));
        }

        Collections.sort(res);

        return res;
    }

    @Override
    public List<Plan> getRecyclePlanByDate(CalendarDate calendarDate) {
        List<Map<String, String>> temp;

        temp = getRecyclePlanDataByDate(calendarDate.getDateString());

        List<Plan> res = new ArrayList<>(temp.size());

        String dateString = calendarDate.getDateString();

        for(Map<String, String> element : temp) {
            res.add(createPlan(element, dateString));
        }

        Collections.sort(res);

        return res;
    }

    @Override
    public Plan getCurrentPlan() {
        Plan plan;

        String dateString = new CalendarDate().getDateString();

        plan = createPlan(getCurrentPlanData(), dateString);

        return plan;
    }

    @Override
    public Plan getUpComingPlan() {
        Plan plan;

        String dateString = new CalendarDate().getDateString();

        plan = createPlan(getUpComingPlanData(), dateString);

        return plan;
    }

    @Override
    public Plan getNormalPlanByID(int id) {
        Plan plan;

        plan = createPlan(getNormalPlanDataByID(id), "2000-12-12");

        return plan;
    }

    @Override
    public Plan getRecyclePlanByID(int id) {
        Plan plan;

        plan = createPlan(getRecyclePlanDataByID(id), "2000-12-12");

        return plan;
    }

    @Override
    public void updatePlan(Plan plan) {
        ContentValues cv = new ContentValues();

        cv.put("content", plan.getContent());
        cv.put("time", plan.getDate().getTimeString());
        cv.put("isAlarm", plan.isAlarm());
        cv.put("isShake", plan.isShake());
        cv.put("isNotify", plan.isNotify());

        //when the plan is set to be normal plan
        if(plan.getDayOfWeek() == 0) {
            cv.put("date", plan.getDate().getDateString());
            //if the plan's recycle flag is set, but the day of week is -1
            //the plan must be originally in the recycle plan table
            //then we have to remove the one in the recycle plan table
            //and add it to the normal plan table
            if(plan.isRecycle()) {
                removeRecyclePlanData(plan.getId());
                addPlan(plan);
            }
            //if the plan is already in the normal plan table
            //just update the one in the table
            else {
                updateNormalPlanData(plan.getId(), cv);
            }
        }
        //when the plan is set to be recycle plan
        else {
            cv.put("week", dayOfWeekToString(plan.getDayOfWeek()));
            //if the original plan is already a recycle plan
            //just update it
            if(plan.isRecycle()) {
                updateRecyclePlanData(plan.getId(), cv);
            }
            //else, remove the one in the normal plan table
            //and add it to the recycle plan table
            else {
                removeNormalPlanData(plan.getId());
                addPlan(plan);
            }
        }
    }

    @Override
    public void removePlan(Plan plan) {
        if(plan.isRecycle()) {
            removeRecyclePlanData(plan.getId());
        }
        else {
            removeNormalPlanData(plan.getId());
        }
    }

    private String dayOfWeekToString(int dayOfWeek) {
        StringBuilder sb = new StringBuilder(7);

        for(int i = 0; i < 7; i++) {
            int mask = 1 << i;
            sb.append((mask & dayOfWeek) != 0 ? '1' : '0');
        }

        return sb.toString();
    }

    @Override
    public void addPlan(Plan plan) {
        ContentValues cv = new ContentValues();

        cv.put("content", plan.getContent());
        cv.put("time", plan.getDate().getTimeString());
        cv.put("isAlarm", plan.isAlarm());
        cv.put("isShake", plan.isShake());
        cv.put("isNotify", plan.isNotify());

        //when the plan is set to be normal plan
        if(plan.getDayOfWeek() == 0) {
            cv.put("date", plan.getDate().getDateString());
            plan.setId(addNormalPlanData(cv));
            plan.setRecycle(false);
        }
        //when the plan is set to be recycle plan
        else {
            cv.put("week", dayOfWeekToString(plan.getDayOfWeek()));
            plan.setId(addRecyclePlanData(cv));
            plan.setRecycle(true);
        }
    }
}
