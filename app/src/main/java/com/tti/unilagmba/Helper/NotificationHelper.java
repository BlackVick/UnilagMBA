package com.tti.unilagmba.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.tti.unilagmba.R;

/**
 * Created by Scarecrow on 4/1/2018.
 */

public class NotificationHelper extends ContextWrapper {
    private static final String USLA_CHANNEL_ID = "com.tti.unilagmba";
    private static final String USLA_CHANNEL_NAME = "Unilag-MBA";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel uslaChannel = new NotificationChannel(USLA_CHANNEL_ID,
                USLA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        uslaChannel.enableLights(true);
        uslaChannel.enableVibration(true);
        uslaChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);


        getManager().createNotificationChannel(uslaChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getUslaChannelNotification(String title, String body, PendingIntent contentIntent,
                                                              Uri soundUri){
        return new Notification.Builder(getApplicationContext(), USLA_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                .setSound(soundUri)
                .setAutoCancel(true);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getUslaChannelNotification(String title, String body, Uri soundUri){
        return new Notification.Builder(getApplicationContext(), USLA_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                .setSound(soundUri)
                .setAutoCancel(true);
    }
}
