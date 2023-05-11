package com.example.mob_dev_portfolio;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MidnightReceiver extends BroadCastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if(hour == 12 && minute == 15){
            mainActivity.reset(context);
        }
    }
}
