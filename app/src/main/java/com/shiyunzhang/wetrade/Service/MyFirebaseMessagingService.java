package com.shiyunzhang.wetrade.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shiyunzhang.wetrade.Activity.AuctionActivity;
import com.shiyunzhang.wetrade.R;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public final static String TAG = "MyFirebaseMessaging";
    public final static String NOTIFICATION_CHANNEL_ID = "com.shiyun.wetrade.test";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().get("end").equals("no")) {
            createNotificationChannel();
            setUpNotification(remoteMessage.getData());
        } else if (remoteMessage.getData().get("end").equals("yes")) {
            createNotificationChannel();
            setUpNotificationForWinner(remoteMessage.getData());

        }
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.getString(R.string.app_name);
            String description = this.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setUpNotification(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String auctionId = data.get("auctionId");
        Intent intent = new Intent(this, AuctionActivity.class);
        intent.putExtra("AUCTIONID", auctionId);
        intent.putExtra("USERTYPE", "Participant");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    public void setUpNotificationForWinner(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String auctionId = data.get("auctionId");
        Intent intent = new Intent(this, AuctionActivity.class);
        intent.putExtra("AUCTIONID", auctionId);
        intent.putExtra("USERTYPE", "Participant");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
