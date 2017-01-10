package com.leodd.oneweek.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leodd.oneweek.BO.IPlanBO;
import com.leodd.oneweek.BO.PlanBO;
import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.Utils.CalendarDate;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Leodd on 2017/1/2.
 */
public class AlarmService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //first, we should attain the up coming plan today
        IPlanBO planBO = new PlanBO(getApplicationContext());
        Plan plan = planBO.getUpComingPlan();

        if(plan == null) {
            //if the there's no up coming plan, set service alarm
            //service alarm is responsible for maintaining the time line
            //while alert alarm is responsible for showing alert like pop up alarm activity
            //or showing notification

            clearAlertAlarm();

            //the date for the service to wake up is the next day morning
            //which is next day 00:00
            CalendarDate date = new CalendarDate();
            date.setTime(date.toString() + " 00:00");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            setServiceAlarm(calendar.getTime());
        }
        else {
            //else, set alert alarm
            setAlertAlarm(plan.getDate());
        }

        AlarmServiceReceiver.completeWakefulIntent(intent);

        return START_NOT_STICKY;
    }

    private void setServiceAlarm(Date date) {
        Context context = getApplicationContext();

        Intent intent = new Intent(context, AlarmServiceReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if(Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        }
    }

    private void setAlertAlarm(Date date) {
        Context context = getApplicationContext();

        Intent intent = new Intent(context, AlertReciver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if(Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        }
    }

    private void clearAlertAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlertReciver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }
}
