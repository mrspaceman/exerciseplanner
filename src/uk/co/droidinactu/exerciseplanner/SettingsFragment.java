package uk.co.droidinactu.exerciseplanner;

import uk.co.droidinactu.exerciseplanner.planviewer.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public final class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public static final String PREF_TIME_AM = "pref_time_am";
	public static final String PREF_TIME_AM_DEFAULT = "07:00";

	public static final String PREF_TIME_NOON = "pref_time_noon";
	public static final String PREF_TIME_NOON_DEFAULT = "13:00";

	public static final String PREF_TIME_PM = "pref_time_pm";
	public static final String PREF_TIME_PM_DEFAULT = "19:00";

	private String formatTimeStr(final String timeStr) {
		String frmtdStr = "";
		if (timeStr.length() < 5) {
			final String[] timeParts = timeStr.split(":");
			if (timeParts[0].length() == 1) {
				frmtdStr = "0";
			}
			frmtdStr += timeParts[0] + ":";
			if (timeParts[1].length() == 1) {
				frmtdStr += "0";
			}
			frmtdStr += timeParts[1];
		}
		return frmtdStr;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getPreferenceManager();
		final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		Preference connectionPref = findPreference(PREF_TIME_AM);
		String str1Fmt = getResources().getString(R.string.pref_time_am_description);
		String timeStr = sharedPrefs.getString(PREF_TIME_AM, PREF_TIME_AM_DEFAULT);
		str1Fmt = String.format(str1Fmt, formatTimeStr(timeStr));
		connectionPref.setSummary(str1Fmt);

		connectionPref = findPreference(PREF_TIME_NOON);
		str1Fmt = getResources().getString(R.string.pref_time_noon_description);
		timeStr = sharedPrefs.getString(PREF_TIME_NOON, PREF_TIME_NOON_DEFAULT);
		str1Fmt = String.format(str1Fmt, formatTimeStr(timeStr));
		connectionPref.setSummary(str1Fmt);

		connectionPref = findPreference(PREF_TIME_PM);
		str1Fmt = getResources().getString(R.string.pref_time_pm_description);
		timeStr = sharedPrefs.getString(PREF_TIME_PM, PREF_TIME_PM_DEFAULT);
		str1Fmt = String.format(str1Fmt, formatTimeStr(timeStr));
		connectionPref.setSummary(str1Fmt);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(final SharedPreferences sharedPrefs, final String key) {
		if (key.equals(SettingsFragment.PREF_TIME_AM)) {
			final Preference connectionPref = findPreference(key);
			String str1Fmt = getResources().getString(R.string.pref_time_am_description);
			final String timeStr = sharedPrefs.getString(key, PREF_TIME_AM_DEFAULT);
			str1Fmt = String.format(str1Fmt, formatTimeStr(timeStr));
			connectionPref.setSummary(str1Fmt);

		} else if (key.equals(SettingsFragment.PREF_TIME_NOON)) {
			final Preference connectionPref = findPreference(key);
			String str1Fmt = getResources().getString(R.string.pref_time_noon_description);
			final String timeStr = sharedPrefs.getString(key, PREF_TIME_NOON_DEFAULT);
			str1Fmt = String.format(str1Fmt, formatTimeStr(timeStr));
			connectionPref.setSummary(str1Fmt);

		} else if (key.equals(SettingsFragment.PREF_TIME_PM)) {
			final Preference connectionPref = findPreference(key);
			String str1Fmt = getResources().getString(R.string.pref_time_pm_description);
			final String timeStr = sharedPrefs.getString(key, PREF_TIME_PM_DEFAULT);
			str1Fmt = String.format(str1Fmt, formatTimeStr(timeStr));
			connectionPref.setSummary(str1Fmt);
		}
	}
}
