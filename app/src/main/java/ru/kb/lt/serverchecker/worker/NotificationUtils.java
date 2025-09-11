package ru.kb.lt.serverchecker.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import ru.kb.lt.serverchecker.view.activivty.MainActivity;

public class NotificationUtils {
    public static final String CHANNEL_ID = "server_monitor_channel";
    public  static final int NOTIFICATION_ID = 1;

    public static void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Мониторинг сервера",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Уведомления о состоянии сервера");

        NotificationManager notificationManager =
                context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void showServerDownNotification(Context context, String serverName) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Server unavailable!")
                .setContentText(serverName + " server not responding")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notificationManager =
                context.getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}