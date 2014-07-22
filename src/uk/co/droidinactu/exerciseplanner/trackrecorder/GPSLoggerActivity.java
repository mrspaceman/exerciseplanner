package uk.co.droidinactu.exerciseplanner.trackrecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import uk.co.droidinactu.exerciseplanner.planviewer.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GPSLoggerActivity extends Activity {

	private static final String tag = "GPSLoggerActivity";
	private static final String tripFileName = "currentTrip.txt";
	private String currentTripName = "";
	private int altitudeCorrectionMeters = 20;
	private final DecimalFormat sevenSigDigits = new DecimalFormat("0.#######");

	private final OnClickListener mStartListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			startService(new Intent(GPSLoggerActivity.this, GPSLoggerService.class));
		}
	};

	private final OnClickListener mStopListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			stopService(new Intent(GPSLoggerActivity.this, GPSLoggerService.class));
		}
	};

	private final OnClickListener mNewTripListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			doNewTripDialog();
		}
	};

	private final OnClickListener mToggleDebugListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			final boolean currentDebugState = GPSLoggerService.isShowingDebugToast();
			GPSLoggerService.setShowingDebugToast(!currentDebugState);
			final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.gpslogger_activity_ToggleButtonDebug);
			toggleButton.setChecked(!currentDebugState);
		}
	};

	private final OnClickListener mExportListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			doExport();
		}
	};

	private void closeFileBuf(final StringBuffer fileBuf, final String beginTimestamp, final String endTimestamp) {
		fileBuf.append("        </coordinates>\n");
		fileBuf.append("     </LineString>\n");
		fileBuf.append("	 <TimeSpan>\n");
		final String formattedBeginTimestamp = zuluFormat(beginTimestamp);
		fileBuf.append("		<begin>" + formattedBeginTimestamp + "</begin>\n");
		final String formattedEndTimestamp = zuluFormat(endTimestamp);
		fileBuf.append("		<end>" + formattedEndTimestamp + "</end>\n");
		fileBuf.append("	 </TimeSpan>\n");
		fileBuf.append("    </Placemark>\n");
		fileBuf.append("  </Document>\n");
		fileBuf.append("</kml>");
	}

	private void doExport() {
		// export the db contents to a kml file
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			final EditText editAlt = (EditText) findViewById(R.id.gpslogger_activity_EditTextAltitudeCorrection);
			altitudeCorrectionMeters = Integer.parseInt(editAlt.getText().toString());
			Log.i(tag, "altitude Correction updated to " + altitudeCorrectionMeters);
			db = openOrCreateDatabase(GPSLoggerService.DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
			cursor = db.rawQuery("SELECT * " + " FROM " + GPSLoggerService.POINTS_TABLE_NAME + " ORDER BY GMTTIMESTAMP ASC", null);
			final int gmtTimestampColumnIndex = cursor.getColumnIndexOrThrow("GMTTIMESTAMP");
			final int latitudeColumnIndex = cursor.getColumnIndexOrThrow("LATITUDE");
			final int longitudeColumnIndex = cursor.getColumnIndexOrThrow("LONGITUDE");
			final int altitudeColumnIndex = cursor.getColumnIndexOrThrow("ALTITUDE");
			final int accuracyColumnIndex = cursor.getColumnIndexOrThrow("ACCURACY");
			if (cursor.moveToFirst()) {
				final StringBuffer fileBuf = new StringBuffer();
				String beginTimestamp = null;
				String endTimestamp = null;
				String gmtTimestamp = null;
				initFileBuf(fileBuf, initValuesMap());
				do {
					gmtTimestamp = cursor.getString(gmtTimestampColumnIndex);
					if (beginTimestamp == null) {
						beginTimestamp = gmtTimestamp;
					}
					final double latitude = cursor.getDouble(latitudeColumnIndex);
					final double longitude = cursor.getDouble(longitudeColumnIndex);
					final double altitude = cursor.getDouble(altitudeColumnIndex) + altitudeCorrectionMeters;
					final double accuracy = cursor.getDouble(accuracyColumnIndex);
					fileBuf.append(sevenSigDigits.format(longitude) + "," + sevenSigDigits.format(latitude) + "," + altitude + "\n");
				} while (cursor.moveToNext());
				endTimestamp = gmtTimestamp;
				closeFileBuf(fileBuf, beginTimestamp, endTimestamp);
				final String fileContents = fileBuf.toString();
				Log.d(tag, fileContents);
				final File sdDir = new File("/sdcard/GPSLogger");
				sdDir.mkdirs();
				final File file = new File("/sdcard/GPSLogger/" + currentTripName + ".kml");
				final FileWriter sdWriter = new FileWriter(file, false);
				sdWriter.write(fileContents);
				sdWriter.close();
				Toast.makeText(getBaseContext(), "Export completed!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getBaseContext(), "I didn't find any location points in the database, so no KML file was exported.",
						Toast.LENGTH_LONG).show();
			}
		} catch (final FileNotFoundException fnfe) {
			Toast.makeText(
					getBaseContext(),
					"Error trying access the SD card.  Make sure your handset is not connected to a computer and the SD card is properly installed",
					Toast.LENGTH_LONG).show();
		} catch (final Exception e) {
			Toast.makeText(getBaseContext(), "Error trying to export: " + e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	private void doNewTrip() {
		SQLiteDatabase db = null;
		try {
			doExport();
			db = openOrCreateDatabase(GPSLoggerService.DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
			db.execSQL("DELETE FROM " + GPSLoggerService.POINTS_TABLE_NAME);
			final EditText tripNameEditor = (EditText) findViewById(R.id.gpslogger_activity_EditTextTripName);
			saveTripName(tripNameEditor.getText().toString());
		} catch (final Exception e) {
			Log.e(tag, e.toString());
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	private void doNewTripDialog() {
		final AlertDialog.Builder ad = new AlertDialog.Builder(GPSLoggerActivity.this);
		ad.setTitle("Whammo!");
		ad.setMessage("Are you sure that you want to start anew?");
		ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				doNewTrip();
			}
		});
		ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				initTripName();
			}
		});
		ad.show();
	}

	public int getAltitudeCorrectionMeters() {
		return altitudeCorrectionMeters;
	}

	private void initFileBuf(final StringBuffer fileBuf, final HashMap valuesMap) {
		fileBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		fileBuf.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
		fileBuf.append("  <Document>\n");
		fileBuf.append("    <name>" + valuesMap.get("FILENAME") + "</name>\n");
		fileBuf.append("    <description>GPSLogger KML export</description>\n");
		fileBuf.append("    <Style id=\"yellowLineGreenPoly\">\n");
		fileBuf.append("      <LineStyle>\n");
		fileBuf.append("        <color>7f00ffff</color>\n");
		fileBuf.append("        <width>4</width>\n");
		fileBuf.append("      </LineStyle>\n");
		fileBuf.append("      <PolyStyle>\n");
		fileBuf.append("        <color>7f00ff00</color>\n");
		fileBuf.append("      </PolyStyle>\n");
		fileBuf.append("    </Style>\n");
		fileBuf.append("    <Placemark>\n");
		fileBuf.append("      <name>Absolute Extruded</name>\n");
		fileBuf.append("      <description>Transparent green wall with yellow points</description>\n");
		fileBuf.append("      <styleUrl>#yellowLineGreenPoly</styleUrl>\n");
		fileBuf.append("      <LineString>\n");
		fileBuf.append("        <extrude>" + valuesMap.get("EXTRUDE") + "</extrude>\n");
		fileBuf.append("        <tessellate>" + valuesMap.get("TESSELLATE") + "</tessellate>\n");
		fileBuf.append("        <altitudeMode>" + valuesMap.get("ALTITUDEMODE") + "</altitudeMode>\n");
		fileBuf.append("        <coordinates>\n");
	}

	private void initTripName() {
		// see if there's currently a trip in the trip file
		String tripName = "new";
		try {
			final FileInputStream fIn = openFileInput(tripFileName);
			final InputStreamReader isr = new InputStreamReader(fIn);
			final char[] inputBuffer = new char[1024];
			isr.read(inputBuffer);
			isr.close();
			fIn.close();
			tripName = new String(inputBuffer).trim();
			Log.i(tag, "loaded trip name: " + tripName);
		} catch (final FileNotFoundException fnfe) {
			Log.i(tag, "first run, no " + tripFileName);
			try {
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMMdd");
				tripName = sdf.format(new Date());
				saveTripName(tripName);
			} catch (final Exception e) {
				Log.e(tag, e.toString());
			}
		} catch (final IOException ioe) {
			Log.e(tag, ioe.toString());
		}
		final EditText tripNameEditor = (EditText) findViewById(R.id.gpslogger_activity_EditTextTripName);
		tripNameEditor.setText(tripName);
		currentTripName = tripName;
	}

	private HashMap initValuesMap() {
		final HashMap valuesMap = new HashMap();

		valuesMap.put("FILENAME", currentTripName);

		final RadioButton airButton = (RadioButton) findViewById(R.id.gpslogger_activity_RadioAir);
		if (airButton.isChecked()) {
			// use air settings
			valuesMap.put("EXTRUDE", "1");
			valuesMap.put("TESSELLATE", "0");
			valuesMap.put("ALTITUDEMODE", "absolute");
		} else {
			// use ground settings for the export
			valuesMap.put("EXTRUDE", "0");
			valuesMap.put("TESSELLATE", "1");
			valuesMap.put("ALTITUDEMODE", "clampToGround");
		}

		return valuesMap;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gpslogger_activity);

		Button button = (Button) findViewById(R.id.gpslogger_activity_ButtonStart);
		button.setOnClickListener(mStartListener);
		button = (Button) findViewById(R.id.gpslogger_activity_ButtonStop);
		button.setOnClickListener(mStopListener);
		button = (Button) findViewById(R.id.gpslogger_activity_ButtonExport);
		button.setOnClickListener(mExportListener);
		final RadioButton ground = (RadioButton) findViewById(R.id.gpslogger_activity_RadioGround);
		ground.setChecked(true);
		initTripName();
		button = (Button) findViewById(R.id.gpslogger_activity_ButtonNewTrip);
		button.setOnClickListener(mNewTripListener);
		final ToggleButton toggleDebug = (ToggleButton) findViewById(R.id.gpslogger_activity_ToggleButtonDebug);
		toggleDebug.setOnClickListener(mToggleDebugListener);
		toggleDebug.setChecked(GPSLoggerService.isShowingDebugToast());
		final EditText editAltitudeCorrection = (EditText) findViewById(R.id.gpslogger_activity_EditTextAltitudeCorrection);
		editAltitudeCorrection.setText(String.valueOf(altitudeCorrectionMeters));
	}

	private void saveTripName(final String tripName) throws FileNotFoundException, IOException {
		final FileOutputStream fOut = openFileOutput(tripFileName, MODE_PRIVATE);
		final OutputStreamWriter osw = new OutputStreamWriter(fOut);
		osw.write(tripName);
		osw.flush();
		osw.close();
		fOut.close();
	}

	public void setAltitudeCorrectionMeters(final int altitudeCorrectionMeters) {
		this.altitudeCorrectionMeters = altitudeCorrectionMeters;
	}

	private String zuluFormat(final String beginTimestamp) {
		// turn 20081215135500 into 2008-12-15T13:55:00Z
		final StringBuffer buf = new StringBuffer(beginTimestamp);
		buf.insert(4, '-');
		buf.insert(7, '-');
		buf.insert(10, 'T');
		buf.insert(13, ':');
		buf.insert(16, ':');
		buf.append('Z');
		return buf.toString();
	}
}
