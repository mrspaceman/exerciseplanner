package uk.co.droidinactu.exerciseplanner.trackrecorder;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import uk.co.droidinactu.common.model.AbstractDataObject;
import uk.co.droidinactu.exerciseplanner.planviewer.db.WorkoutType;

public final class Track extends AbstractDataObject {

	@Attribute(name = "nbr")
	public int dayNbr = -1;

	@Attribute(name = "distanceMeters")
	public double distanceMeters = -1;

	@Attribute(name = "durationMinutes")
	public double durationMinutes = -1;

	@Attribute(name = "altitudeClimedMeters")
	public double altitudeClimedMeters = -1;

	@Attribute(name = "caloriesBurned")
	public int caloriesBurned = -1;

	@Attribute(name = "heartRateAverage")
	public int heartRateAverage = -1;

	@Attribute(name = "description")
	public String description = "";

	@Attribute(name = "trackType")
	public WorkoutType trackType = null;

	@Attribute(name = "startTime")
	public DateTime startTime = new DateTime();

	@ElementList
	public List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();

}
