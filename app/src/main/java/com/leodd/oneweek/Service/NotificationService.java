package com.leodd.oneweek.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leodd.oneweek.BO.NotificationBO;
import com.leodd.oneweek.Beans.PlanBean;
import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.TimeObject;

import java.util.Calendar;

/**
 * Created by Leodd
 * on 2016/6/5.
 */
public class NotificationService extends Service {
    private NotificationBO mNotificationBO;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mNotificationBO = new NotificationBO(getBaseContext());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        final int flag = bundle.getInt("NOTIFICATION_SERVICE_CONTROLLER");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(flag == 1 || flag == 2) {
                    mNotificationBO.clearPlanBeanList();
                }

                if(flag == 2) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    int minute = calendar.get(Calendar.MINUTE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    TimeObject current = new TimeObject(hour, minute);

                    PlanBean planBean = mNotificationBO.getCurrentPlanBeanByTime(current);

                    if(planBean != null) {
                        showNotification(planBean.getContent());
                    }
                }

                findNextTimeAndSetAlarm();
            }
        }).start();

        return super.onStartCommand(intent, 1, startId);
    }

    private void setAlarm(Calendar calendar) {
        Intent i = new Intent();
        i.setAction("com.leodd.oneweek.NOTIFICATION_BROADCAST");
        PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }

    private void findNextTimeAndSetAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        PlanBean planBean = mNotificationBO.getNextPlanBeanByTime(new TimeObject(hour, minute));

        if(planBean != null) {
            calendar.set(Calendar.HOUR_OF_DAY, planBean.getTime().getHour());
            calendar.set(Calendar.MINUTE, planBean.getTime().getMinute());
            calendar.set(Calendar.SECOND, 3);

            setAlarm(calendar);
            return;
        }

//        calendar.add(Calendar.MINUTE, 1);
//        calendar.set(Calendar.SECOND, 3);
//        setAlarm(calendar);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 3);
        calendar.add(Calendar.SECOND, 60);

        setAlarm(calendar);
    }

    private void showNotification(String text) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getBaseContext());
        builder.setSmallIcon(R.drawable.ticker_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))
                .setContentTitle("计划提醒=。=")
                .setContentText(text)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound_notification))
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
        Notification notification;
        if(Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        }
        else {
            notification = builder.getNotification();
        }
        notificationManager.notify(0, notification);
    }
}
