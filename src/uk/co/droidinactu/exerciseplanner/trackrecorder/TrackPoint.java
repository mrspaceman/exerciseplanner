package uk.co.droidinactu.exerciseplanner.trackrecorder;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import uk.co.droidinactu.common.model.AbstractDataObject;
import android.location.Location;

public class TrackPoint extends AbstractDataObject {

	@Attribute(name = "nbr")
	public int heartRate;

	@Element
	public Location location;

}
