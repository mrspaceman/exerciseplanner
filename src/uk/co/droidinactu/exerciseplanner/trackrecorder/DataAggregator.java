package uk.co.droidinactu.exerciseplanner.trackrecorder;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public final class DataAggregator {

	private static DataAggregator instance = null;

	public static DataAggregator getInstance() {
		if (instance == null) {
			instance = new DataAggregator();
			instance.trackRecording = new Track();
			instance.hrRecord = new HeartRateRecord();
			instance.workoutTrack = new ArrayList<Location>();
		}
		return instance;
	}

	public float totalDistanceMeters;

	private List<Location> workoutTrack = null;
	private Track trackRecording = null;
	private HeartRateRecord hrRecord = null;

	private DataAggregator() {
	}

	public synchronized void addPointToTrack(final TrackPoint newPoint) {
		instance.trackRecording.trackPoints.add(newPoint);
	}

	public synchronized void addToWorkoutTrack(final Location location) {
		instance.workoutTrack.add(location);
	}

	public synchronized HeartRateRecord getHrRecord() {
		return hrRecord;
	}

	public synchronized Track getTrack() {
		return instance.trackRecording;
	}

	public synchronized void setHrRecord(final HeartRateRecord hrRecord) {
		instance.hrRecord = hrRecord;
	}

	public synchronized void setTrack(final Track trackRecording) {
		instance.trackRecording = trackRecording;
	}

}
