package uk.co.droidinactu.common.model;

/// <summary>
/// Description of AbstractDataObj.

import java.util.Observable;

import org.joda.time.DateTime;

import uk.co.droidinactu.common.DroidInActuApplication;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * this is the base class of all objects that are stored in a database.
 * 
 * @author andy
 */
public abstract class AbstractDataObject extends Observable implements BaseColumns {
	private static final String LOG_TAG = AbstractDataObj.class.getSimpleName();

	/**
	 * Make a SimpleDateFormat for toString()'s output. This has short (text)
	 * date, a space, short (text) month, a space, 2-digit date, a space, hour
	 * (0-23), minute, second, a space, short timezone, a final space, and a
	 * long year.
	 */
	public static final String dateFormatString = "yyyy MM dd HH:mm:ss";
	public static final String FIELD_NAME_LAST_UPDATED = "lastUpdated";

	/**
	 * this number denotes the version of this class. so that we can decide if
	 * the structure of this class is the same as the one being deserialized
	 * into it
	 */
	private static final long serialVersionUID = 1L;

	public String lastUpdated;
	public long uniqueIdentifier = -1;

	/**
	 * Simple default constructor for data objects
	 */
	public AbstractDataObject() {
		lastUpdated = new DateTime().toString(DroidInActuApplication.simpleDateFmtStrDb);
	}

	public AbstractDataObject(final Cursor results) {
		uniqueIdentifier = results.getInt(results.getColumnIndex(BaseColumns._ID));
		lastUpdated = results.getString(results.getColumnIndex(FIELD_NAME_LAST_UPDATED));
	}

	/**
	 * @return the lastUpdated
	 */
	public String getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * This property allows access to the object's unique identifier
	 */
	public final long getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void resetUniqueIdentifier() {
		uniqueIdentifier = -1;
	}

	/**
	 * @param lastUpdated
	 *            the lastUpdated to set
	 */
	public void setLastUpdated(final DateTime lastUpdatedStr) {
		lastUpdated = lastUpdatedStr.toString(DroidInActuApplication.simpleDateFmtStrDb);
	}

	public void setLastUpdated(final long time) {
		this.setLastUpdated("" + time);
	}

	/**
	 * @param lastUpdated
	 *            the lastUpdated to set
	 */
	public void setLastUpdated(final String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public final void setUniqueIdentifier(final int value) {
		uniqueIdentifier = value;
	}

	public final void setUniqueIdentifier(final long value) {
		uniqueIdentifier = value;
	}

	public final void setUniqueIdentifier(final String value) {
		uniqueIdentifier = Long.parseLong(value);
	}

} // class()

