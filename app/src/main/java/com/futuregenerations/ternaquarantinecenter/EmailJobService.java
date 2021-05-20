package com.futuregenerations.ternaquarantinecenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.*;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class EmailJobService extends JobService {
    public static final String TAG ="EmailJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG,"Job Started");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,15);
        calendar.set(Calendar.MINUTE,55);
        calendar.set(Calendar.SECOND,0);
        Intent alarmIntent = new Intent(getApplicationContext(),AutoEmailReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),20,alarmIntent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),120000L,pendingIntent);

        jobFinished(jobParameters,true);

        Log.d(TAG,"Job Finished");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        onStartJob(jobParameters);
        Log.d(TAG,"Job Restarted");
        return true;
    }
}
