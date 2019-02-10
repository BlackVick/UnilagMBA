package com.tti.unilagmba.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tti.unilagmba.ChatActivity;
import com.tti.unilagmba.Documents;
import com.tti.unilagmba.FriendRequestActivity;
import com.tti.unilagmba.Helper.NotificationHelper;
import com.tti.unilagmba.Login;
import com.tti.unilagmba.MainActivity;
import com.tti.unilagmba.Registration;
import com.tti.unilagmba.R;

import java.util.Map;
import java.util.Random;

/**
 * Created by Scarecrow on 4/1/2018.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationAPI26(remoteMessage);
            else
                sendNotification(remoteMessage);
        }
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        PendingIntent pendingIntent;
        NotificationHelper helper;
        Notification.Builder builder;



        if (title.equalsIgnoreCase("New Friend")) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getUslaChannelNotification(title, message, pendingIntent, defaultSoundUri);
            helper.getManager().notify(0, builder.build());

        } else if (title.equalsIgnoreCase("Friend Request")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getUslaChannelNotification(title, message, pendingIntent, defaultSoundUri);
            helper.getManager().notify(1, builder.build());

        } else if (title.equalsIgnoreCase("New Message")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getUslaChannelNotification(title, message, pendingIntent, defaultSoundUri);
            helper.getManager().notify(2, builder.build());

        } else if (title.equalsIgnoreCase("New Material")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getUslaChannelNotification(title, message, pendingIntent, defaultSoundUri);
            helper.getManager().notify(3, builder.build());

        } else if (title.equalsIgnoreCase("News Alert")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getUslaChannelNotification(title, message, pendingIntent, defaultSoundUri);
            helper.getManager().notify(4, builder.build());

        } else {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getUslaChannelNotification(title, message, pendingIntent, defaultSoundUri);
            helper.getManager().notify(5, builder.build());
        }







    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        String user_id = data.get("user_id");
        String user_name = data.get("user_name");
        String courseId = data.get("courseId");



        if (title.equalsIgnoreCase("New Friend")) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SelectedTabIndex", 3);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.unilag_logo))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());

        } else if (title.equalsIgnoreCase("Friend Request")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SelectedTabIndex", 3);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.unilag_logo))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, builder.build());

        } else if (title.equalsIgnoreCase("New Message")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SelectedTabIndex", 3);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.unilag_logo))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(3, builder.build());

        } else if (title.equalsIgnoreCase("New Material")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SelectedTabIndex", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.unilag_logo))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(4, builder.build());

        } else if (title.equalsIgnoreCase("News Alert")){

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SelectedTabIndex", 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.unilag_logo))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(5, builder.build());

        } else {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_unilag_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.unilag_logo))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(6, builder.build());
        }

    }
}
