package uk.co.droidinactu.exerciseplanner.planviewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
	public static final String LOG_TAG = TimePreference.class.getSimpleName();

	public static int getHour(final String time) {
		int hour = 0;
		try {
			String[] pieces = time.split(":");
			if (pieces.length == 1 && time.indexOf(".") > 0) {
				pieces = time.split(".");
			}
			hour = Integer.parseInt(pieces[0]);
		} catch (final ArrayIndexOutOfBoundsException aioob) {
			Log.wtf(LOG_TAG, "getHour(" + time + ") exception : ", aioob);
		}
		return hour;
	}

	public static int getMinute(final String time) {
		int minute = 0;
		try {
			final String[] pieces = time.split(":");
			minute = Integer.parseInt(pieces[1]);
		} catch (final ArrayIndexOutOfBoundsException aioob) {
			Log.wtf(LOG_TAG, "getMinute(" + time + ") exception : ", aioob);
		}
		return minute;
	}

	private int lastHour = 0;
	private int lastMinute = 0;
	private TimePicker picker = null;

	public TimePreference(final Context ctxt, final AttributeSet attrs) {
		super(ctxt, attrs);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
	}

	@Override
	protected void onBindDialogView(final View v) {
		super.onBindDialogView(v);

		picker.setCurrentHour(lastHour);
		picker.setCurrentMinute(lastMinute);
	}

	@Override
	protected View onCreateDialogView() {
		picker = new TimePicker(getContext());

		return picker;
	}

	@Override
	protected void onDialogClosed(final boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			lastHour = picker.getCurrentHour();
			lastMinute = picker.getCurrentMinute();

			final String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);

			if (callChangeListener(time)) {
				persistString(time);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(final TypedArray a, final int index) {
		return a.getString(index);
	}

	@Override
	protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
		String time = null;

		if (restoreValue) {
			if (defaultValue == null) {
				time = getPersistedString("00:00");
			} else {
				time = getPersistedString(defaultValue.toString());
			}
		} else {
			time = defaultValue.toString();
		}

		lastHour = getHour(time);
		lastMinute = getMinute(time);
	}
}
