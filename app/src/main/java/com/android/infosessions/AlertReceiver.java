package com.android.infosessions;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Thao on 6/3/17.
 */

public class AlertReceiver extends BroadcastReceiver {
    String[] value;
    String title;
    String time;
    String location;
    @Override
    public void onReceive(Context context, Intent intent) {
        value = intent.getStringExtra("VALUE").split(",");
        title = value[0];
        time = value[1];
        location = value[2];
        createNotification(context, "Info session: ", "Go by: ", "Alert");
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert) {
        PendingIntent notifIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(msg + title)
                .setTicker(msgAlert)
                .setContentText(msgText + time + " At: " + location);

        mBuilder.setContentIntent(notifIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int mNotificationId = 1;
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }
}
