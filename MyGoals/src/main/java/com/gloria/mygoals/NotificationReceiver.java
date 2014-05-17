package com.gloria.mygoals;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class NotificationReceiver extends BroadcastReceiver {

    // Notification ID to allow for future updates
    private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "NotificationReceiver";

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    // Notification Sound and Vibration on Arrival
    /*private Uri soundURI = Uri
            .parse("android.resource://course.examples.Alarms.AlarmCreate/"
                    + R.raw.alarm_rooster);
    private long[] mVibratePattern = { 0, 200, 200, 300 };*/

    @Override
    public void onReceive(Context context, Intent intent) {

        CharSequence tickerText = context.getString(R.string.notif_ticker_text);// "Are you ready to start your task?"
        CharSequence contentTitle = context.getString(R.string.notif_title);    // "A task will start in 15 min";
        CharSequence contentText = context.getString(R.string.notif_text);      // "One more step to your goal achievement!";

        mNotificationIntent = new Intent(context, MainActivity.class);
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context).setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle(contentTitle)
                .setContentText(contentText).setContentIntent(mContentIntent);
                //.setSound(soundURI).setVibrate(mVibratePattern);

        // Pass the Notification to the NotificationManager:
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.build());

        Log.i(TAG, "Sending notification at:" + DateFormat.getDateTimeInstance().format(new Date()));

    }
}