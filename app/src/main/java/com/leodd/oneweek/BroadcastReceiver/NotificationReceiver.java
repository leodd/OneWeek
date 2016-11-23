package com.leodd.oneweek.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.leodd.oneweek.Service.NotificationService;

/**
 * Created by Leodd
 * on 2016/6/5.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("NOTIFICATION_SERVICE_CONTROLLER", 2);
        i.putExtras(bundle);
        context.startService(i);
    }
}
