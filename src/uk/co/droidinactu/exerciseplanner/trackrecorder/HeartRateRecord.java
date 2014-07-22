package uk.co.droidinactu.exerciseplanner.trackrecorder;

import java.text.DecimalFormat;

import org.joda.time.DateTime;

import uk.co.droidinactu.common.DroidInActuApplication;
import uk.co.droidinactu.common.model.AbstractDataObject;
import uk.co.droidinactu.exerciseplanner.TrainerApplication;
import android.util.Log;

/**
 * [254, 008, 247, 001, 209, 000, 007, 004, 000, 000]
 * 
 * @author aspela
 * 
 */
public class HeartRateRecord extends AbstractDataObject {

	private static final String LOG_TAG = "HeartRateRecord";

	public static final DecimalFormat nbrFmt = new DecimalFormat("#000");

	/**
	 * this number denotes the version of this class. so that we can decide if
	 * the structure of this class is the same as the one being deserialized
	 * into it
	 */
	private static final long serialVersionUID = 1L;

	private final static int BYTE_HEART_RATE = 5;
	private final static int BYTE_BATTERY_LEVEL = 4;

	public int byte0; // new record marker

	public int byte1;
	public int byte2;
	public int byte3;
	public int byte4;
	public int byte6;
	public int byte7;
	public int byte8;
	public int byte9;
	public String dateTaken = null;

	public int heartRate;
	public int batteryLevel;

	public HeartRateRecord() {
	}

	public String getDateTakenString() {
		return TrainerApplication.sdf.format(dateTaken);
	}

	public void parseBytes(final int[] bytes) {

		byte0 = bytes[0];
		byte1 = bytes[1];
		byte2 = bytes[2];
		byte3 = bytes[3];
		batteryLevel = bytes[BYTE_BATTERY_LEVEL];
		heartRate = bytes[BYTE_HEART_RATE];
		byte6 = bytes[6];
		byte7 = bytes[7];
		byte8 = bytes[8];
		byte9 = bytes[9];

		String byteStr = "bytes = [";
		for (final int b : bytes) {
			byteStr += nbrFmt.format(b) + ", ";
		}
		Log.i(LOG_TAG, byteStr + "]");

	}

	public void setDateTaken(final DateTime dateTaken) {
		this.dateTaken = dateTaken.toString(DroidInActuApplication.simpleDateFmtStrDb);
	}

	public void setDateTaken(final String string) {
		dateTaken = string;
	}
}
