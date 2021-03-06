package com.android.infosessions;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.infosessions.data.SessionContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by Thao on 6/3/17.
 */

public class AlertReceiver extends BroadcastReceiver {
    String[] value;
    String title;
    String time;
    String location;
    Uri mUri;
    int mId;
    @Override
    public void onReceive(Context context, Intent intent) {
        value = intent.getStringExtra("VALUE").split(";");
        title = value[0];
        time = value[1];
        location = value[2];
        mUri = Uri.parse(value[3]);
        mId = Integer.parseInt(value[4]);
        createNotification(context, "Alert");
    }

    public void createNotification(Context context, String msgAlert) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.setData(mUri);

        PendingIntent notifIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setTicker(msgAlert)
                .setContentText(location + " at " + time);

        mBuilder.setContentIntent(notifIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        // Each element then alternates between delay, vibrate, sleep, vibrate, sleep
        mBuilder.setVibrate(new long[] { 0, 200, 100, 200, 0 });

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random rand = new Random();

        int  mNotificationId = rand.nextInt(Integer.MAX_VALUE) + Integer.MIN_VALUE;

        //int mNotificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        mNotificationManager.notify(mId, mBuilder.build());
    }
}
