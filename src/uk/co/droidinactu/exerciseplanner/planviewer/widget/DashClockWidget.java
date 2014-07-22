package uk.co.droidinactu.exerciseplanner.planviewer.widget;

import org.joda.time.DateTime;

import uk.co.droidinactu.exerciseplanner.TrainerApplication;
import uk.co.droidinactu.exerciseplanner.planviewer.PlanViewerActivity;
import uk.co.droidinactu.exerciseplanner.planviewer.R;
import uk.co.droidinactu.exerciseplanner.planviewer.db.Workout;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public final class DashClockWidget extends DashClockExtension {
	public static final String LOG_TAG = DashClockWidget.class.getSimpleName();

	@Override
	protected void onUpdateData(final int reason) {

		final TrainerApplication myApp = TrainerApplication.getInstance();

		DateTime today = new DateTime();
		today = today.withTime(0, 0, 0, 0);

		Workout wrkout = myApp.model.getWorkout(today);
		if (wrkout == null) {
			today = today.plusDays(1);
			wrkout = myApp.model.getWorkout(today);
		}

		if (wrkout != null) {

			final DateTime.Property pDoW = today.dayOfWeek();
			String str1Fmt = getResources().getString(R.string.widget_dashclock_title_field);
			String str2Fmt = getResources().getString(R.string.widget_dashclock_body_field);

			String duration = "";
			if (wrkout.durationMinutes > 0) {
				duration = TrainerApplication.minsFmt.format(wrkout.durationMinutes) + " mins";
			} else if (wrkout.distanceKm > 0) {
				duration = TrainerApplication.kmFmt.format(wrkout.distanceKm) + " km";
			}

			str1Fmt = String.format(str1Fmt, pDoW.getAsShortText(), wrkout.type.toString());
			final Spanned title = Html.fromHtml(str1Fmt);

			str2Fmt = String.format(str2Fmt, duration, wrkout.effort);
			final Spanned expandedBody = Html.fromHtml(str2Fmt);

			// Publish the extension data update.
			publishUpdate(new ExtensionData().visible(true).icon(wrkout.getTypeResourceId())
					.status(wrkout.type.toString()).expandedTitle(title.toString())
					.expandedBody(expandedBody.toString())
					.clickIntent(new Intent(getApplicationContext(), PlanViewerActivity.class)));
		}
	}
}
