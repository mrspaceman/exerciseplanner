package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import uk.co.droidinactu.common.model.AbstractDataObject;

@Root
public final class Week extends AbstractDataObject {

	@Attribute(name = "nbr")
	public int weekNbr = -1;

	@ElementList
	public List<Day> days = new ArrayList<Day>();

	public Week() {
	}

	@Override
	public String toString() {
		return "Week : " + weekNbr;
	}

	public double totalDistance() {
		double totalD = 0;
		for (final Day dy : days) {
			totalD += dy.getTotalDistance();
		}
		return totalD;
	}

	public double totalDuration() {
		double totalD = 0;
		for (final Day dy : days) {
			totalD += dy.getTotalDuration();
		}
		return totalD;
	}
}
