package uk.co.droidinactu.exerciseplanner;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import uk.co.droidinactu.common.DroidInActuApplication;
import uk.co.droidinactu.exerciseplanner.planviewer.db.DataModel;
import android.util.Log;

public final class TrainerApplication extends DroidInActuApplication {
	public static final String LOG_TAG = TrainerApplication.class.getSimpleName();

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final DecimalFormat minsFmt = new DecimalFormat("#0");
	public static final DecimalFormat kmFmt = new DecimalFormat("#0.0");

	private static TrainerApplication singleton;

	public static TrainerApplication getInstance() {
		return TrainerApplication.singleton;
	}

	public DataModel model;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate(); application being created.");
		TrainerApplication.singleton = this;
		model = new DataModel(getApplicationContext());
		model.initialise(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		model.shutdown();
		model = null;
	}
}
