package uk.co.droidinactu.exerciseplanner.planviewer;

import java.util.List;

import org.joda.time.DateTime;

import uk.co.droidinactu.exerciseplanner.TrainerApplication;
import uk.co.droidinactu.exerciseplanner.planviewer.db.Day;
import uk.co.droidinactu.exerciseplanner.planviewer.db.Workout;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DayExpandableListRowAdaptor extends BaseExpandableListAdapter {

	private final class ChildViewHolder {
		ImageView img;
		TextView txtName;
		TextView txtDuration;
		TextView txtDesc;
	}

	private final Context context;
	private final List<Day> rows;

	private final DateTime weekStartDate;

	public DayExpandableListRowAdaptor(final Context context, final List<Day> groups, final DateTime weekDate) {
		Log.i(PlanViewerActivity.LOG_TAG, "DayExpandableListRowAdaptor::DayExpandableListRowAdaptor [" + groups.size() + "] days");
		this.context = context;
		rows = groups;
		weekStartDate = weekDate;
	}

	@Override
	public Workout getChild(final int groupPosition, final int childPosition) {
		Workout tmpWrkout = rows.get(groupPosition).getWorkout(childPosition);
		if (tmpWrkout == null && childPosition == 1) {
			tmpWrkout = rows.get(groupPosition).getWorkout(2);
		}
		return tmpWrkout;
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		return rows.get(groupPosition).getNbrWorkouts();
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView,
			final ViewGroup parent) {
		View row = convertView;
		ChildViewHolder holder;

		if (row == null) {
			final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.workout_list_row, parent, false);

			holder = new ChildViewHolder();

			holder.img = (ImageView) row.findViewById(R.id.trainingplan_row_type_img);
			holder.txtName = (TextView) row.findViewById(R.id.trainingplan_row_workout_name);
			holder.txtDuration = (TextView) row.findViewById(R.id.trainingplan_row_workout_duration);
			holder.txtDesc = (TextView) row.findViewById(R.id.trainingplan_row_workout_description);

			row.setTag(holder);
		} else {
			holder = (ChildViewHolder) row.getTag();

		}
		final Workout wrkout = getChild(groupPosition, childPosition);
		final DateTime now = new DateTime().withTime(0, 0, 0, 0);

		if (weekStartDate.plusDays(groupPosition).equals(now)) {
			row.setBackgroundResource(R.drawable.today_border);
		} else {
			row.setBackgroundResource(R.drawable.day_border);
		}

		if (wrkout != null) {
			holder.img.setImageResource(wrkout.getTypeResourceId());
			holder.txtName.setText(wrkout.type.toString());

			if (wrkout.durationMinutes > 0) {
				holder.txtDuration.setText(TrainerApplication.minsFmt.format(wrkout.durationMinutes) + " mins");
			} else if (wrkout.distanceKm > 0) {
				holder.txtDuration.setText(TrainerApplication.kmFmt.format(wrkout.distanceKm) + " km");
			} else {
				holder.txtDuration.setText("");
			}
			holder.txtDesc.setText(wrkout.text);
		}
		return row;
	}

	@Override
	public Day getGroup(final int groupPosition) {
		return rows.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return rows.size();
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			final LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inf.inflate(R.layout.workout_list_group, null);
		}

		final TextView dayName = (TextView) convertView.findViewById(R.id.trainingplan_row_group_dayname);
		final TextView nbrWorkouts = (TextView) convertView.findViewById(R.id.trainingplan_row_group_nbrWorkouts);

		final Day group = getGroup(groupPosition);

		final DateTime now = new DateTime().withTime(0, 0, 0, 0);
		if (weekStartDate.plusDays(groupPosition).equals(now)) {
			convertView.setBackgroundResource(R.drawable.today_border);
		} else {
			convertView.setBackgroundResource(R.drawable.day_border);
		}

		final DateTime.Property pDoW = weekStartDate.plusDays(groupPosition).dayOfWeek();
		dayName.setText(pDoW.getAsShortText());

		String str2Fmt = context.getResources().getString(R.string.list_row_header_workouts);
		str2Fmt = String.format(str2Fmt, group.getNbrWorkouts());
		final Spanned result = Html.fromHtml(str2Fmt);
		nbrWorkouts.setText(result);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return false;
	}

}
