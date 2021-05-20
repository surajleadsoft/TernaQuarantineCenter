package com.futuregenerations.ternaquarantinecenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textViewName, textViewPowered;
    public static final int SPLASH_TIMED_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);

        setAutoEmail();


//        boolean hasScheduled = false;
//        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
//            if (jobInfo.getId() == 123) {
//                hasScheduled = true;
//            }
//        }
//
//        if (!hasScheduled) {
//            ComponentName componentName = new ComponentName(this,EmailJobService.class);
//            JobInfo jobInfo1 = new JobInfo.Builder(123,componentName)
//                    .setRequiresCharging(true)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                    .setPeriodic(60000L)
//                    .setPersisted(true)
//                    .build();
//
//            int resultCode = scheduler.schedule(jobInfo1);
//            if (resultCode == JobScheduler.RESULT_SUCCESS) {
//                Log.d("EmailJobService","Job Scheduled");
//            }
//            else {
//                Log.d("EmailJobService","Job Scheduling Failed");
//            }
//        }
//        else {
//            Toast.makeText(this, "Already Scheduled", Toast.LENGTH_SHORT).show();
//        }

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        imageView = findViewById(R.id.image_logo);
        textViewName = findViewById(R.id.text_app_name);
        textViewPowered = findViewById(R.id.text_powered_by);

        Animation animationImage = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoomout);
        Animation animationText = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        imageView.startAnimation(animationImage);
        textViewName.startAnimation(animationText);
        textViewPowered.startAnimation(animationText);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        },SPLASH_TIMED_OUT);
    }

    private void setAutoEmail() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,16);
        calendar.set(Calendar.MINUTE,30);
        Intent alarmIntent = new Intent(getApplicationContext(),AutoEmailReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),204,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(),204,alarmIntent,PendingIntent.FLAG_NO_CREATE))!=null;
        if (alarmUp) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
//            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }
        else {
//            Toast.makeText(this, "Alarm Not Set", Toast.LENGTH_SHORT).show();
        }
    }
}
