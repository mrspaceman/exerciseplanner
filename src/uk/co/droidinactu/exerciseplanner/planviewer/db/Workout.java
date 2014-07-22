package uk.co.droidinactu.exerciseplanner.planviewer.db;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import uk.co.droidinactu.common.model.AbstractDataObject;
import uk.co.droidinactu.exerciseplanner.planviewer.R;

@Root
public final class Workout extends AbstractDataObject {

	public long day_id = -1;

	@Attribute(name = "duration", required = false)
	public double durationMinutes = 0;

	@Attribute(name = "duration2", required = false)
	public double duration2Minutes = 0;

	@Attribute(name = "duration3", required = false)
	public double duration3Minutes = 0;

	@Attribute(name = "distance", required = false)
	public double distanceKm = 0;

	@Attribute(name = "distance2", required = false)
	public double distance2Km = 0;

	@Attribute(name = "distance3", required = false)
	public double distance3Km = 0;

	@Attribute(name = "type", required = false)
	public String type = WorkoutType.REST.toString();

	@Attribute(name = "effort", required = false)
	public String effort = WorkoutType.REST.toString();

	@Text(empty = "n/a", required = false)
	public String text = "";

	public Workout() {
	}

	public int getEffortResourceId() {
		if (effort != null) {
			if (effort.equalsIgnoreCase("Easy")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("Steady")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("Gentle")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("Hard")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("Hills")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("Tempo")) {
				return R.drawable.exercise_type_tempo;
			} else if (effort.equalsIgnoreCase("Fartlek")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("Race Pace")) {
				return R.drawable.exercise_type_racepace;
			} else if (effort.equalsIgnoreCase("Race")) {
				return R.drawable.exercise_type_race;
			} else if (effort.equalsIgnoreCase("Long Slow")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("Repeats")) {
				return R.drawable.exercise_type_run;
			} else if (effort.equalsIgnoreCase("CrossTrain") || effort.equalsIgnoreCase("Cross Train")) {
				return R.drawable.exercise_type_cycle;
			} else if (effort.equalsIgnoreCase("Swim")) { return R.drawable.exercise_type_swim; }
		}
		return R.drawable.exercise_type_rest;
	}

	public String getText() {
		return text;
	}

	public double getTotalDistance() {
		int retValue = 0;
		retValue += distanceKm;
		retValue += distance2Km;
		retValue += distance3Km;
		return retValue;
	}

	public double getTotalDuration() {
		int retValue = 0;
		retValue += durationMinutes;
		retValue += duration2Minutes;
		retValue += duration3Minutes;
		return retValue;
	}

	public int getTypeResourceId() {
		switch (WorkoutType.getFromString(type)) {
		case BRICK:
			return R.drawable.exercise_type_brick;
		case CYCLE:
			return R.drawable.exercise_type_cycle;
		case CYCLE_INDOOR:
			return R.drawable.exercise_type_cycle_stationary;
		case RACE:
			return R.drawable.exercise_type_race;
		case RACE_TRI:
			return R.drawable.exercise_type_race;
		case RUN:
			return R.drawable.exercise_type_run;
		case SWIM:
			return R.drawable.exercise_type_swim;
		case WEIGHTS:
			return R.drawable.exercise_type_weights;
		case XTRAIN:
			return R.drawable.exercise_type_xtrain;
		default:
			return R.drawable.exercise_type_rest;
		}
	}

	public void setText(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return type.toString() + " " + effort;
	}
}
