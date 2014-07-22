package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import uk.co.droidinactu.common.DroidInActuApplication;
import uk.co.droidinactu.common.model.AbstractDataModel;
import uk.co.droidinactu.exerciseplanner.planviewer.PlanViewerActivity;
import android.content.Context;
import android.util.Log;

public final class DataModel extends AbstractDataModel {
	public static final String DB_NAME = "trainingplanner.db4o";
	private static DataModel instance = null;
	private static final String LOG_TAG = DataModel.class.getSimpleName() + "::";

	public static DataModel getInstance(final Context ctx) {
		if (instance == null) {
			instance = new DataModel(ctx);
		}
		return DataModel.instance;
	}

	private final Context context;

	private final DatabaseHelper databaseHelper = null;

	public TrainingPlan trainPlan = null;

	private DataModel(final Context ctx) {
		context = ctx;
	}

	private DateTime calculatePlanStartDate() {
		DateTime today = new DateTime();
		today = today.withTime(0, 0, 0, 0);
		final DateTime raceMonday = today.withDayOfWeek(DateTimeConstants.MONDAY);
		return raceMonday;
	}

	private DateTime calculatePlanStartDate(final DateTime raceDate) {
		raceDate.getDayOfWeek();
		final DateTime raceMonday = raceDate.withDayOfWeek(DateTimeConstants.MONDAY);
		return raceMonday.plusWeeks(1 - Integer.parseInt(trainPlan.weeksDuration));
	}

	public Workout getNextWorkout() {
		DateTime today = new DateTime();
		today = today.withTime(0, 0, 0, 0);
		Workout wk = getWorkout(today);
		if (wk == null) {
			today = today.plusDays(1);
			wk = getWorkout(today);
		}
		return wk;
	}

	public Week getWeek(final DateTime weekStartDate) {
		return trainPlan.getWeek(weekStartDate);
	}

	public List<Week> getWeeks() {
		return trainPlan.weeks;
	}

	public Workout getWorkout(final DateTime today) {
		final DateTime monday = today.withDayOfWeek(DateTimeConstants.MONDAY);
		final Week wk = getWeek(monday);
		if (wk != null) {
			for (final Day dy : wk.days) {
				final DateTime tmpDate = monday.plusDays(dy.dayNbr - 1);
				if (tmpDate.equals(today)) { return dy.getNextWorkout(context, monday); }
			}
		}
		return null;
	}

	public void importTrainingPlan(final Context ctx, final InputStream xmlStream) {
		if (container == null) {
			initialise(ctx);
		}
		TrainingPlan newPlan = null;
		try {
			final Serializer serializer = new Persister();
			newPlan = serializer.read(TrainingPlan.class, xmlStream);
			xmlStream.close();
		} catch (final Exception e) {
			Log.e(PlanViewerActivity.LOG_TAG, "DataModel::Exception importing training plan from stream", e);
		}
		try {
			if (trainPlan != null) {
				container.delete(trainPlan);
			}
			trainPlan = newPlan;
			container.store(trainPlan);
			container.commit();
		} catch (final Exception e) {
			Log.e(PlanViewerActivity.LOG_TAG, "DataModel::Exception storing training plan", e);
		}
	}

	public void importTrainingPlan(final Context ctx, final String filename) throws IOException {
		Log.i(PlanViewerActivity.LOG_TAG, "DataModel::Exception importing training plan from [" + filename + "]");
		final File file = new File(filename);
		final FileInputStream fis = new FileInputStream(file);
		importTrainingPlan(ctx, fis);
	}

	@Override
	public void initialise(final Context ctx) {
		Log.i(PlanViewerActivity.LOG_TAG, "DataModel::initialise()");
		database();
		final List<TrainingPlan> plans = container.query(TrainingPlan.class);
		if (plans.size() > 0) {
			trainPlan = plans.get(0);
		}

		// JODBConfig.enablePlatformConfig(PLATFORM_CONFIG.ANDROID);
	}

	public boolean planInitialised() {
		if (trainPlan == null) { return false; }
		return trainPlan.planStartDate != null;
	}

	public void setRaceDate(final Context ctx, final DateTime raceDate) {
		if (container == null) {
			initialise(ctx);
		}
		if (trainPlan != null) {
			trainPlan.raceDate = raceDate.toString(DroidInActuApplication.simpleDateFmtStrDb);
			trainPlan.planStartDate = calculatePlanStartDate(raceDate).toString(
					DroidInActuApplication.simpleDateFmtStrDb);
			container.store(trainPlan);
			container.commit();
		}
	}

	public void setRaceDateNone(final Context ctx) {
		if (container == null) {
			initialise(ctx);
		}
		if (trainPlan != null) {
			trainPlan.raceDate = null;
			trainPlan.planStartDate = calculatePlanStartDate().toString(DroidInActuApplication.simpleDateFmtStrDb);
			container.store(trainPlan);
			container.commit();
		}
	}

	public void shutdown() {
		Log.i(PlanViewerActivity.LOG_TAG, "DataModel::shutdown()");
		if (container != null) {
			container.commit();
			container.close();
		}
	}

}
