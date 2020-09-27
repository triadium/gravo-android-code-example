package ru.gravo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

import ru.gravo.App;
import ru.gravo.ExtraLog;
import ru.gravo.utils.CalendarHelper;

/**
 * Created by Алексей on 31.10.2017.
 */

public class WorkTimeScheduler {
    private AlarmManager alarmManager;

    private final static int ALARM_COUNT = 3;

    private final static int ALARM_ONE_ID = 0;
    private final static int ALARM_TWO_ID = 1;
    private final static int ALARM_THREE_ID = 2;

    public final static int AUTO_CHECK_IN_ID = 1;
    public final static int GET_READY_TO_SHIFT_ID = 2;
    public final static int IN_PLACE_CHECKING_ID = 3;
    public final static int RETURN_TO_PLACE_CHECKING_ID = 4;
    public final static int CONNECTION_CHECKING_ID = 5;
    public final static int SHIFT_ENDED_ID = 6;
    public final static int GET_READY_TO_PAUSE_ID = 7;
    public final static int PAUSE_ENDED_ID = 8;
    public final static int OVERTIME_STARTED_ID = 9;

    public final static String ID_KEY = "ru.gravo.WorkTimeJob.id";

    private Intent intents[];

    public WorkTimeScheduler(){
        Context context = App.getContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intents  = new Intent[ALARM_COUNT];
        for (int i = 0; i < intents.length; i++) {
            intents[i] = new Intent(context, WorkTimeJobReceiver.class);
        }
    }

    public void cancelAllJobs(){
        Context context = App.getContext();
        for (int i = 0; i < intents.length; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intents[i], PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
    }

    private int getAlarmId(int id){
        int alarmId = ALARM_ONE_ID;
        switch (id){
            case AUTO_CHECK_IN_ID:
            case IN_PLACE_CHECKING_ID:
            case RETURN_TO_PLACE_CHECKING_ID:
            case CONNECTION_CHECKING_ID:
                alarmId = ALARM_ONE_ID;
                break;
            case GET_READY_TO_SHIFT_ID:
            case SHIFT_ENDED_ID:
                alarmId = ALARM_TWO_ID;
                break;
            case GET_READY_TO_PAUSE_ID:
            case PAUSE_ENDED_ID:
            case OVERTIME_STARTED_ID:
                alarmId = ALARM_THREE_ID;
                break;
        }
        return alarmId;
    }

    public void startJob(int id, long at){
        int alarmId = getAlarmId(id);

        Intent intent =  intents[alarmId];
        intent.putExtra(ID_KEY, id);

        Context context = App.getContext();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //FIXME: Use JobSchdeuler?
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(at, pendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, at, pendingIntent);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, at, pendingIntent);
        }

        ExtraLog.d("Job" + String.valueOf(alarmId) + " will start at " + CalendarHelper.getTimeStringUTC(at));
    }
}
