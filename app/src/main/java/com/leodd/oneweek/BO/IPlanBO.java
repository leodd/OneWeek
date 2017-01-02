package com.leodd.oneweek.BO;

import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.Utils.CalendarDate;

import java.util.List;

/**
 * Created by leodd on 2016/12/18.
 * this interface defines the methods of plan business object
 */

public interface IPlanBO {

    List<Plan> getPlanByDate(CalendarDate calendarDate);

    List<Plan> getNormalPlanByDate(CalendarDate calendarDate);

    List<Plan> getRecyclePlanByDate(CalendarDate calendarDate);

    Plan getCurrentPlan();

    Plan getUpComingPlan();

    Plan getNormalPlanByID(int id);

    Plan getRecyclePlanByID(int id);

    void updatePlan(Plan plan);

    void removePlan(Plan plan);

    void addPlan(Plan plan);
}
