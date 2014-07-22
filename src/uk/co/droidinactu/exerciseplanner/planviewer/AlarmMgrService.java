package uk.co.droidinactu.exerciseplanner.planviewer;

import java.util.Calendar;

import org.joda.time.DateTime;

import uk.co.droidinactu.exerciseplanner.TrainerApplication;
import uk.co.droidinactu.exerciseplanner.planviewer.db.Workout;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.AlarmClock;

public final class AlarmMgrService extends Service {
	public static final String LOG_TAG = AlarmMgrService.class.getSimpleName();
	private Workout wrkout;

	private Workout getWorkout() {
		final TrainerApplication myApp = TrainerApplication.getInstance();

		DateTime today = new DateTime();
		today = today.withTime(0, 0, 0, 0);

		wrkout = myApp.model.getWorkout(today);
		if (wrkout == null) {
			today = today.plusDays(1);
			wrkout = myApp.model.getWorkout(today);
		}

		return wrkout;
	}

	@Override
	public IBinder onBind(final Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private void setAlarm(final Context ctx, final DateTime alarmTime) {
		final Calendar alarmCal = Calendar.getInstance();
		alarmCal.setTimeInMillis(alarmTime.getMillis());

		// cancel Old Alarm
		// mAlarmPendingIntent = PendingIntent.getActivity(this, requestCode,
		// intent, flags);
		// ctx.getAlarmManager().cancel(mAlarmPendingIntent);

		final Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);

		alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, wrkout.toString());

		alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, alarmCal.get(Calendar.HOUR_OF_DAY));
		alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, alarmCal.get(Calendar.MINUTE));
		alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
		startActivity(alarmIntent);
	}

}
