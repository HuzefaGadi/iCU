package huzefagadi.com.icu;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
    	try {

			Intent intentForBackground = new Intent(context, BackgroundService.class);
			context.startService(intentForBackground);

		} catch (Exception e) {

		}
    }


}