package uk.co.droidinactu.exerciseplanner.planviewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.openintents.intents.FileManagerIntents;

import uk.co.droidinactu.common.DroidInActuApplication;
import uk.co.droidinactu.exerciseplanner.SettingsActivity;
import uk.co.droidinactu.exerciseplanner.TrainerApplication;
import uk.co.droidinactu.exerciseplanner.planviewer.db.Week;
import uk.co.droidinactu.exerciseplanner.trackrecorder.GpsService;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class PlanViewerActivity extends Activity implements ActionBar.OnNavigationListener, View.OnClickListener {

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case GpsService.MSG_NEW_LOCATION:
				final Location aLoc = msg.getData().getParcelable("newLocation");
				// FIXME: textStrValue.setText("Str Message: " + str1);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * 
	 * @author aspela
	 * 
	 */
	public static class PlanOverviewFragment extends Fragment implements OnClickListener {

		String[] titles = new String[] { "mins", "Km" };

		private LinearLayout chartLayout;

		public PlanOverviewFragment() {
		}

		protected XYMultipleSeriesRenderer buildBarRenderer(final int[] colors) {
			final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
			renderer.setAxisTitleTextSize(16);
			renderer.setChartTitleTextSize(20);
			renderer.setLabelsTextSize(15);
			renderer.setLegendTextSize(15);
			final int length = colors.length;
			for (int i = 0; i < length; i++) {
				final SimpleSeriesRenderer r = new SimpleSeriesRenderer();
				r.setColor(colors[i]);
				renderer.addSeriesRenderer(r);
			}
			return renderer;
		}

		@Override
		public void onClick(final View v) {
		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_plan_overview, container, false);

			chartLayout = (LinearLayout) rootView.findViewById(R.id.achart);

			planOvervwFrgmntInit = true;
			update();

			return rootView;
		}

		private void update() {
			if (!planOvervwFrgmntInit || !TrainerApplication.getInstance().model.planInitialised()) { return; }

			updateStackedGraph();
		}

		private void updateStackedGraph() {
			int maxDurationAndDistance = 0;
			final double[] durations = new double[myApp.model.trainPlan.weeks.size()];
			final double[] distances = new double[myApp.model.trainPlan.weeks.size()];
			for (final Week wk : myApp.model.trainPlan.weeks) {
				if (wk.totalDuration() + wk.totalDistance() > maxDurationAndDistance) {
					maxDurationAndDistance = (int) (wk.totalDuration() + wk.totalDistance());
				}
				durations[wk.weekNbr - 1] = wk.totalDuration();
				distances[wk.weekNbr - 1] = wk.totalDistance();
			}

			final List<double[]> values = new ArrayList<double[]>();
			values.add(durations);
			values.add(distances);

			final int[] colors = new int[] { Color.BLUE, Color.CYAN };
			final XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
			renderer.setChartTitle("Training Plan Summary");
			renderer.setXTitle("Week");
			renderer.setYTitle("Total");
			renderer.setXAxisMin(0);
			renderer.setXAxisMax(durations.length);
			renderer.setYAxisMin(0);
			renderer.setYAxisMax(maxDurationAndDistance);
			renderer.setAxesColor(Color.GRAY);
			renderer.setLabelsColor(Color.MAGENTA);
			renderer.setLabelsTextSize(22);
			renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
			renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
			renderer.setXLabels(durations.length);
			renderer.setYLabels(5);
			renderer.setXLabelsAlign(Align.LEFT);
			renderer.setYLabelsAlign(Align.LEFT);
			renderer.setPanEnabled(true, false);
			// renderer.setZoomEnabled(false);
			renderer.setZoomRate(1.1f);
			renderer.setBarSpacing(0.5f);

			final XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			final int length = titles.length;
			for (int i = 0; i < length; i++) {
				final CategorySeries series = new CategorySeries(titles[i]);
				final double[] v = values.get(i);
				final int seriesLength = v.length;
				for (int k = 0; k < seriesLength; k++) {
					series.add(v[k]);
				}
				dataset.addSeries(series.toXYSeries());
			}

			final GraphicalView mChart = ChartFactory.getBarChartView(getActivity(), dataset, renderer, Type.STACKED);
			chartLayout.addView(mChart);
		}
	}

	public static class PlanViewerFragment extends Fragment implements OnClickListener {

		public ExpandableListView weekList;
		public TextView startDateTxt;
		public TextView summaryTxt;
		public DateTime currViewedWeek;
		public TextView startDateNbrTxt;
		public ImageButton btnWeekNext;
		public ImageButton btnWeekPrev;

		public PlanViewerFragment() {
		}

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.trainingplan_week_btn_week_next:
				currViewedWeek = currViewedWeek.plusWeeks(1);
				update(currViewedWeek);
				break;
			case R.id.trainingplan_week_btn_week_prev:
				currViewedWeek = currViewedWeek.plusWeeks(-1);
				update(currViewedWeek);
				break;
			}
		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_plan_viewer, container, false);

			currViewedWeek = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY);
			currViewedWeek = currViewedWeek.withTime(0, 0, 0, 0);

			weekList = (ExpandableListView) rootView.findViewById(R.id.trainingplan_week_list);
			startDateTxt = (TextView) rootView.findViewById(R.id.trainingplan_week_start_date);
			startDateNbrTxt = (TextView) rootView.findViewById(R.id.trainingplan_week_start_nbr);
			summaryTxt = (TextView) rootView.findViewById(R.id.trainingplan_week_summary);

			btnWeekPrev = (ImageButton) rootView.findViewById(R.id.trainingplan_week_btn_week_prev);
			btnWeekNext = (ImageButton) rootView.findViewById(R.id.trainingplan_week_btn_week_next);

			btnWeekPrev.setOnClickListener(this);
			btnWeekNext.setOnClickListener(this);

			planViewFrgmntInit = true;
			update();

			return rootView;
		}

		private void update() {
			currViewedWeek = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY);
			currViewedWeek = currViewedWeek.withTime(0, 0, 0, 0);
			update(currViewedWeek);
		}

		private void update(final DateTime showWeekDate) {
			if (!planViewFrgmntInit || !TrainerApplication.getInstance().model.planInitialised()) { return; }

			final Week currWeek = TrainerApplication.getInstance().model.getWeek(showWeekDate);

			if (currWeek == null) { return; }
			btnWeekPrev.setEnabled(currWeek.weekNbr > 1);

			String str1Fmt = getResources().getString(R.string.week_start_nbr);
			str1Fmt = String.format(str1Fmt, currWeek.weekNbr,
					TrainerApplication.getInstance().model.trainPlan.weeksDuration);
			Spanned result = Html.fromHtml(str1Fmt);
			startDateNbrTxt.setText(result);

			String str2Fmt = getResources().getString(R.string.week_start_date);
			str2Fmt = String.format(str2Fmt, showWeekDate.toString(DroidInActuApplication.simpleDateFmtStrView));
			result = Html.fromHtml(str2Fmt);
			startDateTxt.setText(result);

			String str3Fmt = getResources().getString(R.string.week_summary);
			str3Fmt = String.format(str3Fmt, currWeek.totalDistance(), currWeek.totalDuration());
			result = Html.fromHtml(str3Fmt);
			summaryTxt.setText(result);

			if (currWeek != null) {
				final DayExpandableListRowAdaptor adapter = new DayExpandableListRowAdaptor(getActivity(),
						currWeek.days, showWeekDate);
				weekList.setAdapter(adapter);
				final DateTime tmpDate = new DateTime().withTime(0, 0, 0, 0);
				int firstExpandedDay = new DateTime().getDayOfWeek() - 1;
				if (!showWeekDate.equals(tmpDate.withDayOfWeek(DateTimeConstants.MONDAY))) {
					firstExpandedDay = 0;
				}
				for (int i = firstExpandedDay; i < adapter.getGroupCount(); i++) {
					weekList.expandGroup(i);
				}
			}
		}
	}

	/**
	 * 
	 * @author aspela
	 * 
	 */
	public static class WorkoutHistoryFragment extends Fragment implements OnClickListener {

		public WorkoutHistoryFragment() {
		}

		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub

		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_workout_history, container, false);

			wrkoutHistoryFrgmntInit = true;
			update();

			return rootView;
		}

		public void update() {
		}
	}

	/**
	 * 
	 * @author aspela
	 * 
	 */
	public static class WorkoutRecorderFragment extends Fragment implements OnClickListener {

		private DateTime trackStarted;
		private Timer timer;
		private TextView distanceTotal;
		private TextView stopwatchTime;
		private TextView stopwatchLap;
		private Button buttonStart;
		private Button buttonStop;
		private Button buttonLap;
		private List<Location> workoutTrack;
		private final Location lastLocation = null;
		private float totalDistanceMeters;
		private boolean recordingStarted;
		private ImageView locationStatusIcon;

		PeriodFormatter fmt = new PeriodFormatterBuilder().printZeroAlways().minimumPrintedDigits(2).appendHours()
				.appendSeparator(":").printZeroAlways().minimumPrintedDigits(2).appendMinutes().appendSeparator(":")
				.printZeroAlways().minimumPrintedDigits(2).appendSeconds().toFormatter();

		public WorkoutRecorderFragment() {
		}

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.wrkout_recrdr_btn_start:
				recordingStart();
				break;
			case R.id.wrkout_recrdr_btn_stop:
				recordingStop();
				break;
			case R.id.wrkout_recrdr_btn_lap:
				recordLapPoint();
				break;
			}

		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_workout_recorder, container, false);

			distanceTotal = (TextView) rootView.findViewById(R.id.wrkout_recrdr_distance_total);
			stopwatchTime = (TextView) rootView.findViewById(R.id.wrkout_recrdr_stopwatch_time);
			stopwatchLap = (TextView) rootView.findViewById(R.id.wrkout_recrdr_stopwatch_lap);
			locationStatusIcon = (ImageView) rootView.findViewById(R.id.wrkout_recrdr_location_status);

			buttonStart = (Button) rootView.findViewById(R.id.wrkout_recrdr_btn_start);
			buttonStop = (Button) rootView.findViewById(R.id.wrkout_recrdr_btn_stop);
			buttonLap = (Button) rootView.findViewById(R.id.wrkout_recrdr_btn_lap);

			buttonStart.setOnClickListener(this);
			buttonStop.setOnClickListener(this);
			buttonLap.setOnClickListener(this);

			wrkoutRcrdrFrgmntInit = true;
			update();

			return rootView;
		}

		@Override
		public void onPause() {
			super.onPause();
		}

		private void recordingStart() {
			recordingStarted = true;
			trackStarted = new DateTime();
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					updateStopwatchDisplay();
				}
			}, 0, 100L);

			/*
			 * Creates a new Intent to start the RSSPullService IntentService.
			 * Passes a URI in the Intent's "data" field.
			 */
			gpsServiceIntent = new Intent(getActivity(), GpsService.class);
			getActivity().startService(gpsServiceIntent);
		}

		private void recordingStop() {
			recordingStarted = false;
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			gpsServiceIntent = new Intent(getActivity(), GpsService.class);
			getActivity().stopService(gpsServiceIntent);
		}

		private void recordLapPoint() {
			if (recordingStarted) {
			}
		}

		public void update() {
			if (!wrkoutRcrdrFrgmntInit) { return; }
		}

		private void updateDistanceDisplay() {
			if (!wrkoutRcrdrFrgmntInit) { return; }

			String str3Fmt = getResources().getString(R.string.wrkout_recrdr_distance_total);
			str3Fmt = String.format(str3Fmt, totalDistanceMeters / 100);
			final Spanned result = Html.fromHtml(str3Fmt);

			// runOnUiThread(new Runnable() {
			// @Override
			// public void run() {
			// distanceTotal.setText(result);
			// }
			// });
		}

		private void updateStopwatchDisplay() {
			if (!wrkoutRcrdrFrgmntInit) { return; }
			final DateTime timeNow = new DateTime();
			final Interval interval = new Interval(trackStarted, timeNow);

			// runOnUiThread(new Runnable() {
			// @Override
			// public void run() {
			// stopwatchTime.setText(fmt.print(interval.toPeriod()));
			// }
			// });
		}
	}

	private Messenger mService = null;
	private final Messenger mMessenger = new Messenger(new IncomingHandler());

	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName className, final IBinder service) {
			mService = new Messenger(service);
			// FIXME: textStatus.setText("Attached.");
			try {
				final Message msg = Message.obtain(null, GpsService.MSG_START_RECORDING);
				msg.replyTo = mMessenger;
				mService.send(msg);
			} catch (final RemoteException e) {
				// In this case the service has crashed before we could even do
				// anything with it
			}
		}

		@Override
		public void onServiceDisconnected(final ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected - process crashed.
			mService = null;
			// FIXME: textStatus.setText("Disconnected.");
		}
	};

	private static Intent gpsServiceIntent;

	public static final String LOG_TAG = PlanViewerActivity.class.getSimpleName();
	private static final int FILE_SELECT_CODE = 9632;
	public static boolean planViewFrgmntInit = false;
	public static boolean planOvervwFrgmntInit = false;
	public static boolean wrkoutRcrdrFrgmntInit = false;
	public static boolean wrkoutHistoryFrgmntInit = false;

	private Dialog builtInPlanDialog;
	private static TrainerApplication myApp;
	private PlanViewerFragment planViewFrgmnt = null;
	private PlanOverviewFragment planOvervwFrgmnt = null;
	private WorkoutRecorderFragment wrkoutRcrdrFrgmnt = null;

	private WorkoutHistoryFragment wrkoutHistryFrgmnt = null;
	private boolean planViewFrgmntCurrent;
	private boolean planOvervwFrgmntCurrent;
	private boolean wrkoutHistryFrgmntCurrent;

	private boolean wrkoutRcrdrFrgmntCurrent;

	private boolean isOiFileManagerInstalled() {
		final PackageManager packageManager = getPackageManager();
		final Intent intent = new Intent(FileManagerIntents.ACTION_PICK_FILE);
		final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// obtain the filename
				final Uri fileUri = data.getData();
				if (fileUri != null) {
					final String filePath = fileUri.getPath();
					if (filePath != null) {
						pickTrainingPlan(filePath);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myApp = TrainerApplication.getInstance();

		if (savedInstanceState != null) {
			try {
				planViewFrgmnt = (PlanViewerFragment) getFragmentManager().getFragment(savedInstanceState,
						PlanViewerFragment.class.getName());
			} catch (final Exception e) {
			}
			try {
				planOvervwFrgmnt = (PlanOverviewFragment) getFragmentManager().getFragment(savedInstanceState,
						PlanOverviewFragment.class.getName());
			} catch (final Exception e) {
			}
			try {
				wrkoutRcrdrFrgmnt = (WorkoutRecorderFragment) getFragmentManager().getFragment(savedInstanceState,
						WorkoutRecorderFragment.class.getName());
			} catch (final Exception e) {
			}
			try {
				wrkoutHistryFrgmnt = (WorkoutHistoryFragment) getFragmentManager().getFragment(savedInstanceState,
						WorkoutHistoryFragment.class.getName());
			} catch (final Exception e) {
			}
		}

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final String[] dropdownTitles = new String[] { getString(R.string.title_plan_viewer),
				getString(R.string.title_plan_overview), getString(R.string.title_workout_recorder),
				getString(R.string.title_workout_history), };
		// dropdownTitles = new String[] {
		// getString(R.string.title_plan_viewer),
		// getString(R.string.title_plan_overview), };

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1,
						android.R.id.text1, dropdownTitles), this);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.plan_viewer, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		// When the given dropdown item is selected, show its contents in the
		// container view.

		switch (itemPosition) {
		case 0:
			if (planViewFrgmnt == null) {
				planViewFrgmnt = new PlanViewerFragment();
			}
			planViewFrgmntCurrent = true;
			planOvervwFrgmntCurrent = false;
			wrkoutRcrdrFrgmntCurrent = false;
			wrkoutHistryFrgmntCurrent = false;
			getFragmentManager().beginTransaction().replace(R.id.container, planViewFrgmnt).commit();
			break;
		case 1:
			if (planOvervwFrgmnt == null) {
				planOvervwFrgmnt = new PlanOverviewFragment();
			}
			planViewFrgmntCurrent = false;
			planOvervwFrgmntCurrent = true;
			wrkoutRcrdrFrgmntCurrent = false;
			wrkoutHistryFrgmntCurrent = false;
			getFragmentManager().beginTransaction().replace(R.id.container, planOvervwFrgmnt).commit();
			break;
		case 2:
			if (wrkoutRcrdrFrgmnt == null) {
				wrkoutRcrdrFrgmnt = new WorkoutRecorderFragment();
			}
			planViewFrgmntCurrent = true;
			planOvervwFrgmntCurrent = false;
			wrkoutRcrdrFrgmntCurrent = true;
			wrkoutHistryFrgmntCurrent = false;
			getFragmentManager().beginTransaction().replace(R.id.container, wrkoutRcrdrFrgmnt).commit();
			break;
		case 3:
			if (wrkoutHistryFrgmnt == null) {
				wrkoutHistryFrgmnt = new WorkoutHistoryFragment();
			}
			planViewFrgmntCurrent = true;
			planOvervwFrgmntCurrent = false;
			wrkoutRcrdrFrgmntCurrent = false;
			wrkoutHistryFrgmntCurrent = true;
			getFragmentManager().beginTransaction().replace(R.id.container, wrkoutHistryFrgmnt).commit();
			break;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			final Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			break;
		case R.id.action_jump_to_today:
			planViewFrgmnt.update();
			break;
		case R.id.action_select_plan:
			showTrainingPlanChooser();
			return true;
		case R.id.action_help:
			// showHelp();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (myApp.model.planInitialised()) {
			if (planViewFrgmnt != null) {
				planViewFrgmnt.update();
			}
			if (planOvervwFrgmnt != null) {
				planOvervwFrgmnt.update();
			}
		} else {
			showTrainingPlanChooser();
		}
		// SDCardUtils.test();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		if (planViewFrgmntCurrent) {
			getFragmentManager().putFragment(outState, PlanViewerFragment.class.getName(), planViewFrgmnt);
		}
		if (planOvervwFrgmntCurrent) {
			getFragmentManager().putFragment(outState, PlanOverviewFragment.class.getName(), planOvervwFrgmnt);
		}
		if (wrkoutHistryFrgmntCurrent) {
			getFragmentManager().putFragment(outState, WorkoutHistoryFragment.class.getName(), wrkoutHistryFrgmnt);
		}
		if (wrkoutRcrdrFrgmntCurrent) {
			getFragmentManager().putFragment(outState, WorkoutRecorderFragment.class.getName(), wrkoutRcrdrFrgmnt);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		final Intent longSvc = new Intent(getApplicationContext(), AlarmMgrService.class);
		startService(longSvc);
	}

	private void pickBuiltInTrainingPlan(final String planName) {
		try {
			myApp.model.importTrainingPlan(this, getAssets().open("plans/" + planName));
			pickRaceDate();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void pickRaceDate() {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		final Fragment prev = getFragmentManager().findFragmentByTag("racedatepickerdialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		final RaceDatePickerDialog newFragment = RaceDatePickerDialog.newInstance();
		newFragment.setActivity(this);
		newFragment.show(ft, "racedatepickerdialog");
	}

	private void pickTrainingPlan(final String planName) {
		try {
			myApp.model.importTrainingPlan(this, planName);
			pickRaceDate();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void pickTrainingPlanDefault() {
		pickBuiltInTrainingPlan("RW-Half-Marathon-Intermediate.xml");
	}

	private void preferencesLoad() {

	}

	private void preferencesSave() {

	}

	private void showBuiltInPlanChooser() {
		// set up dialog
		builtInPlanDialog = new Dialog(this);
		builtInPlanDialog.setContentView(R.layout.trainingplan_chooser_dialog);
		builtInPlanDialog.setTitle(getResources().getString(R.string.trainingplan_chooser_title));
		builtInPlanDialog.setCancelable(true);

		// set up button
		final Button button = (Button) builtInPlanDialog.findViewById(R.id.trainingplan_chooser_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Spinner spinner = (Spinner) builtInPlanDialog.findViewById(R.id.trainingplan_chooser_spinner);
				final String planName = (String) spinner.getSelectedItem();
				pickBuiltInTrainingPlan(planName + ".xml");
				builtInPlanDialog.dismiss();
			}
		});
		// now that the dialog is set up, it's time to show it
		builtInPlanDialog.show();
	}

	private void showTrainingPlanChooser() {

		final File extDir = Environment.getExternalStorageDirectory();
		final String trainingPlansPath = extDir.getPath() + File.separator + "TrainingPlanXMLs";
		final File trainingPlansDir = new File(trainingPlansPath);

		if (trainingPlansDir.exists() && trainingPlansDir.isDirectory() && trainingPlansDir.list().length > 0) {
			if (!isOiFileManagerInstalled()) {
				final Intent marketIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://search?q=pname:org.openintents.filemanager"));
				startActivity(marketIntent);
			}
			final Intent intent = new Intent(FileManagerIntents.ACTION_PICK_FILE);

			// Set fancy title and button (optional)
			intent.putExtra(FileManagerIntents.EXTRA_TITLE, getString(R.string.file_chooser_title));
			intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, getString(R.string.file_chooser_button_text));

			try {
				startActivityForResult(intent, FILE_SELECT_CODE);
			} catch (final ActivityNotFoundException e) {

			}
		} else {
			trainingPlansDir.mkdir();
			showBuiltInPlanChooser();
		}
	}

	public void updateRaceDate(final int year, final int month, final int dayOfMonth) {
		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		final Fragment prev = getFragmentManager().findFragmentByTag("racedatepickerdialog");
		if (prev != null) {
			ft.hide(prev);
			ft.remove(prev);
		}
		ft.commit();
		final DateTime tmpDate = new DateTime(year, month, dayOfMonth, 0, 0);
		myApp.model.setRaceDate(this, tmpDate);
		planViewFrgmnt.update();
	}

	public void updateRaceDateNone() {
		myApp.model.setRaceDateNone(this);
		planViewFrgmnt.update();
	}
}
