package com.leodd.oneweek.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.leodd.oneweek.BO.IPlanBO;
import com.leodd.oneweek.BO.PlanBO;
import com.leodd.oneweek.Models.Plan;
import com.leodd.oneweek.R;
import com.leodd.oneweek.UI.AlarmActivity;
import com.leodd.oneweek.UI.MainActivity;
import com.leodd.oneweek.Utils.ScreenLockUtil;

/**
 * Created by leodd on 2017/1/3.
 */

public class AlertReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //the first thing to do is to send alarm service receiver a message
        //so that the time line can be maintain
        Intent alarmServiceIntent = new Intent(
                context,
                AlarmServiceReceiver.class);
        context.sendBroadcast(alarmServiceIntent, null);

        //the second thing to do is to attain the current plan from the data base
        IPlanBO planBO = new PlanBO(context);
        Plan plan = planBO.getCurrentPlan();

        //the final thing to do is to choose the way of alert
        if(plan.isAlarm()) {
            showAlarmActivity(context, plan.getContent(), plan.isShake());
        }
        else {
            showNotification(context, plan.getContent());
        }
    }

    private void showAlarmActivity(Context context, String text, boolean isVibrate) {
        ScreenLockUtil.acquireCpuWakeLock(context);

        Intent intent = new Intent(context, AlarmActivity.class);

        intent.putExtra(AlarmActivity.ARG_VIBRATE, isVibrate);
        intent.putExtra(AlarmActivity.ARG_CONTENT, text);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_USER_ACTION);

        context.startActivity(intent);
    }

    private void showNotification(Context context, String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.alarm_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo))
                .setContentTitle("OneWeek Reminder")
                .setContentText(text)
                .setTicker(text)
                .setContentIntent(contentIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
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
