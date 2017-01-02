package com.leodd.oneweek.BO;

import android.content.ContentValues;

import java.util.List;
import java.util.Map;

/**
 * Created by leodd on 2016/12/17.
 * This interface define the basic business operation of the database.
 */

public interface IServerBO {

    /**
     * get a list of plan that match the given date from the normal plan table
     * @param string the date for searching plan, format: "yyyy-MM-dd"
     * @return all plan data that match
     */
    List<Map<String, String>> getNormalPlanDataByDate(String string);

    /**
     * get a list of plan that match the given date from the recycle plan table
     * @param string the date for searching plan, format: "yyyy-MM-dd"
     * @return all plan data that match
     */
    List<Map<String, String>> getRecyclePlanDataByDate(String string);

    /**
     * get a list of plan that match the given day of week from the recycle plan table
     * @param dayOfWeek a bit string that reflects the day of week
     * @return all plan data that match
     */
    List<Map<String, String>> getRecyclePlanDataByWeek(int dayOfWeek);

    /**
     * get a list of plan that match the given date
     * from both the normal plan table and the recycle plan table
     * @param string the date for searching plan, format: "yyyy-MM-dd"
     * @return all plan data that match
     */
    List<Map<String, String>> getPlanDataByDate(String string);

    /**
     * get the most up coming plan
     * @return data of the up coming plan, if no up coming plan found, return null
     */
    Map<String, String> getUpComingPlanData();

    /**
     * get the current plan data
     * @return data of the current plan, if no current plan found, return null
     */
    Map<String, String> getCurrentPlanData();

    /**
     * get plan from normal plan table by id
     * @param id the identification number of the plan
     * @return data of the plan that matches the given id
     */
    Map<String, String> getNormalPlanDataByID(int id);

    /**
     * get plan from recycle plan table by id
     * @param id the identification number of the plan
     * @return data of the plan that matches the given id
     */
    Map<String, String> getRecyclePlanDataByID(int id);

    /**
     * update the plan that matches the given id in the normal plan table
     * @param id the identification number of the plan
     * @param contentValues the new data
     */
    void updateNormalPlanData(int id, ContentValues contentValues);

    /**
     * update the plan that matches the given id in the recycle plan table
     * @param id the identification number of the plan
     * @param contentValues the new data
     */
    void updateRecyclePlanData(int id, ContentValues contentValues);

    /**
     * add new plan to the normal plan table
     * @param contentValues the data of the new plan
     * @return the identification number of the new plan
     */
    int addNormalPlanData(ContentValues contentValues);

    /**
     * add new plan to the recycle plan table
     * @param contentValues the data of the new plan
     * @return the identification number of the new plan
     */
    int addRecyclePlanData(ContentValues contentValues);

    /**
     * remove the plan from the normal plan table according to the given id
     * @param id the identification number of the plan
     */
    void removeNormalPlanData(int id);

    /**
     * remove the plan from the recycle plan table according to the given id
     * @param id the identification number of the plan
     */
    void removeRecyclePlanData(int id);

    /**
     * clear all the tables in the database
     */
    void clearDataBase();
}
