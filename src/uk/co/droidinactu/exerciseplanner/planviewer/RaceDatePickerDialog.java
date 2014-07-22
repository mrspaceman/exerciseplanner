package uk.co.droidinactu.exerciseplanner.planviewer;

import java.util.Calendar;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

public class RaceDatePickerDialog extends DialogFragment implements OnClickListener {

	static RaceDatePickerDialog newInstance() {
		final RaceDatePickerDialog f = new RaceDatePickerDialog();
		return f;
	}

	PlanViewerActivity actvty = null;
	DatePicker datePkr;

	@Override
	public void onClick(final View v) {
		if (actvty == null) {
			actvty = (PlanViewerActivity) getActivity();
		}
		if (v.getId() == R.id.race_date_picker_btn_done) {
			actvty.updateRaceDate(datePkr.getYear(), datePkr.getMonth() + 1, datePkr.getDayOfMonth());
		} else if (v.getId() == R.id.race_date_picker_btn_no_race) {
			actvty.updateRaceDateNone();
		}
		dismiss();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final int style = DialogFragment.STYLE_NORMAL;
		final int theme = android.R.style.Theme_Holo;
		setStyle(style, theme);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.race_date_picker_dialog, container, false);

		datePkr = (DatePicker) v.findViewById(R.id.race_date_picker_date);
		datePkr.getCalendarView().setFirstDayOfWeek(Calendar.getInstance().getFirstDayOfWeek());
		// datePkr.getCalendarView().setFirstDayOfWeek(Calendar.MONDAY);

		final Button btnDone = (Button) v.findViewById(R.id.race_date_picker_btn_done);
		btnDone.setOnClickListener(this);

		final Button btnNoRace = (Button) v.findViewById(R.id.race_date_picker_btn_no_race);
		btnNoRace.setOnClickListener(this);

		return v;
	}

	public void setActivity(final PlanViewerActivity planViewerActivity) {
		actvty = planViewerActivity;
	}
}
