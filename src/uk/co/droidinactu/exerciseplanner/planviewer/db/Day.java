package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import uk.co.droidinactu.common.model.AbstractDataObj;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.SparseArray;

public final class Day extends AbstractDataObj {

	public static final String DATABASE_TABLE_NAME = "day";
	public static final String FIELD_NAME_DAY_NBR = "day_nbr";
	public static final String FIELD_NAME_DAY_WORKOUT_AM = "workout_am";
	public static final String FIELD_NAME_DAY_WORKOUT_NOON = "workout_noon";
	public static final String FIELD_NAME_DAY_WORKOUT_PM = "workout_pm";

	public static final String PREF_WORKOUT_TIME_AM = "PREF_WORKOUT_TIME_AM";
	public static final String PREF_WORKOUT_TIME_AM_TODAY = "06:00";

	public static final String PREF_WORKOUT_TIME_NOON = "PREF_WORKOUT_TIME_NOON";
	public static final String PREF_WORKOUT_TIME_NOON_TODAY = "12:00";

	public static final String PREF_WORKOUT_TIME_PM = "PREF_WORKOUT_TIME_AM";
	public static final String PREF_WORKOUT_TIME_PM_TODAY = "18:00";

	@Element(name = "AM", required = false)
	public Workout am = null;

	@Attribute(name = "nbr")
	public int dayNbr = -1;

	@Element(name = "NOON", required = false)
	public Workout noon = null;

	@Element(name = "PM", required = false)
	public Workout pm = null;

	public Day() {
	}

	public Day(final Cursor results) {
		super(results);
		dayNbr = Integer.parseInt(results.getString(results.getColumnIndex(FIELD_NAME_DAY_NBR)));
		// am =
		// Integer.parseInt(results.getString(results.getColumnIndex(FIELD_NAME_DAY_WORKOUT_AM)));
		// noon =
		// Integer.parseInt(results.getString(results.getColumnIndex(FIELD_NAME_DAY_WORKOUT_NOON)));
		// pm =
		// Integer.parseInt(results.getString(results.getColumnIndex(FIELD_NAME_DAY_WORKOUT_PM)));
	}

	@Override
	public ContentValues getContentValues() {
		final ContentValues cv = super.getContentValues();
		cv.put(FIELD_NAME_DAY_NBR, dayNbr);
		cv.put(FIELD_NAME_TYRE_MODEL, tyreModel);
		cv.put(FIELD_NAME_TYRE_COST, tyreCost);
		cv.put(FIELD_NAME_TYRE_DATE, tyreDate);
		return cv;
	}

	@Override
	public SparseArray<ArrayList<String>> getFields() {
		final SparseArray<ArrayList<String>> fields = super.getFields();
		int x = fields.size();
		fields.put(x++, getArrayList(FIELD_NAME_DAY_NBR, "number NOT NULL"));
		fields.put(x++, getArrayList(FIELD_NAME_TYRE_MODEL, "VARCHAR(75) NOT NULL"));
		fields.put(x++, getArrayList(FIELD_NAME_TYRE_COST, "VARCHAR(75) NOT NULL"));
		fields.put(x++, getArrayList(FIELD_NAME_TYRE_DATE, "VARCHAR(75) NOT NULL"));
		return fields;
	}

	public int getNbrWorkouts() {
		int nbrWrkOuts = 0;
		if (am != null) {
			nbrWrkOuts++;
		}
		if (noon != null) {
			nbrWrkOuts++;
		}
		if (pm != null) {
			nbrWrkOuts++;
		}
		return nbrWrkOuts;
	}

	public Workout getNextWorkout(final Context ctx, final DateTime monday) {
		final DateTime dayDate = monday.plusDays(dayNbr - 1);
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		final String workoutTimeAm = sp.getString(PREF_WORKOUT_TIME_AM, PREF_WORKOUT_TIME_AM_TODAY);
		final String workoutTimeNoon = sp.getString(PREF_WORKOUT_TIME_NOON, PREF_WORKOUT_TIME_NOON_TODAY);
		final String workoutTimePm = sp.getString(PREF_WORKOUT_TIME_PM, PREF_WORKOUT_TIME_PM_TODAY);

		String[] timeParts = workoutTimeAm.split(":");
		final DateTime todayWithAm = dayDate.withTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]),
				0, 0);

		timeParts = workoutTimeNoon.split(":");
		final DateTime todayWithNoon = dayDate.withTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]),
				0, 0);

		timeParts = workoutTimePm.split(":");
		final DateTime todayWithPm = dayDate.withTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]),
				0, 0);

		final DateTime currTime = new DateTime();
		if (currTime.compareTo(todayWithAm) <= 0) {
			return am;
		} else if (currTime.compareTo(todayWithNoon) <= 0) {
			return noon;
		} else if (currTime.compareTo(todayWithPm) <= 0) { return pm; }

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.co.droidinactu.common.model.AbstractDataObj#getTableName()
	 */
	@Override
	public String getTableName() {
		return DATABASE_TABLE_NAME;
	}

	public double getTotalDistance() {
		int retValue = 0;
		if (am != null) {
			retValue += am.getTotalDistance();
		}
		if (noon != null) {
			retValue += noon.getTotalDistance();
		}
		if (pm != null) {
			retValue += pm.getTotalDistance();
		}
		return retValue;
	}

	public double getTotalDuration() {
		int retValue = 0;
		if (am != null) {
			retValue += am.getTotalDuration();
		}
		if (noon != null) {
			retValue += noon.getTotalDuration();
		}
		if (pm != null) {
			retValue += pm.getTotalDuration();
		}
		return retValue;
	}

	public Workout getWorkout(final int childPosition) {
		if (childPosition == 0) { return am; }
		if (childPosition == 1) { return noon; }
		return pm;
	}

}
