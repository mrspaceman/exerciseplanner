package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Root;

import uk.co.droidinactu.common.DroidInActuApplication;
import uk.co.droidinactu.common.model.AbstractDataObject;

@Root
public final class TrainingPlan extends AbstractDataObject {

	@Attribute
	public String name = "";

	@Attribute(name = "weeksduration")
	public String weeksDuration = "";

	@Attribute(name = "source", required = false)
	public String source = "";

	@ElementUnion({ @Element(name = "Book", type = Book.class), @Element(name = "Magazine", type = Magazine.class),
			@Element(name = "Website", type = Website.class) })
	private Reference reference;

	@Element(name = "Help", required = false)
	private Help help;

	@ElementList
	public List<Week> weeks = new ArrayList<Week>();

	public String planStartDate = null;
	public String actualStartDate = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).toString(
			DroidInActuApplication.simpleDateFmtStrDb);
	public String raceDate = null;

	public TrainingPlan() {
	}

	public Week getWeek(final DateTime weekStartDate) {
		if (planStartDate == null) { return null; }

		DateTime tmpDate = DroidInActuApplication.simpleDateFmt.parseDateTime(planStartDate);
		tmpDate = tmpDate.withTime(0, 0, 0, 0);

		Week wk = null;

		for (final Week w : weeks) {
			if (tmpDate.equals(weekStartDate)) {
				wk = w;
				break;
			}
			tmpDate = tmpDate.plusWeeks(1);
		}
		if (wk == null) {
			wk = weeks.get(0);
			wk.weekNbr = 0;
		}
		return wk;
	}

	@Override
	public String toString() {
		return name + " [" + weeksDuration + " weeks]";
	}

}
