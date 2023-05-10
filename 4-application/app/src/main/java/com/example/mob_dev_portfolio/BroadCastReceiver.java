package com.example.mob_dev_portfolio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class BroadCastReceiver extends BroadcastReceiver {


    MainActivity mainActivity = new MainActivity();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if(hour == 16 && minute == 15){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(notificationManager != null && notificationManager.getNotificationChannel(MainActivity.channelID) == null){
                    NotificationChannel channel = new NotificationChannel(MainActivity.channelID,"example_channel",NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("example notification");
                    notificationManager.createNotificationChannel(channel);
                }
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.channelID)
                    .setSmallIcon(R.drawable.ic_water)
                    .setContentTitle("Water your plants!")
                    .setContentText("Reminder: it's 4pm! Time to water your plants")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManager.notify(0, builder.build());
        }
    }


}
