/**
 * <p>
 * <u><b>Copyright Notice</b></u>
 * </p><p>
 * The copyright in this document is the property of 
 * Bath Institute of Medical Engineering.
 * </p><p>
 * Without the written consent of Bath Institute of Medical Engineering
 * given by Contract or otherwise the document must not be copied, reprinted or
 * reproduced in any material form, either wholly or in part, and the contents
 * of the document or any method or technique available there from, must not be
 * disclosed to any other person whomsoever.
 *  </p><p>
 *  <b><i>Copyright 2013-2014 Bath Institute of Medical Engineering.</i></b>
 * --------------------------------------------------------------------------
 * 
 */
package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.droidinactu.common.model.AbstractDataObj;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.SparseArray;

/**
 * @author andy
 * 
 */
public class DbMetadata extends AbstractDataObj {
	public enum RegistrationStatus {
		NOT_STARTED, REQUEST_SENT, REQUEST_ACCEPTED, REQUEST_DENIED
	}

	private static final String LOG_TAG = DbMetadata.class.getSimpleName() + "::";

	public static final String DATABASE_TABLE_NAME = "dbmetadata";

	public static final String FIELD_NAME_DBMETADATA_TIMESTAMP = "db_timestamp";
	public static final String FIELD_NAME_DBMETADATA_VERSION = "db_version";
	public static final String FIELD_NAME_DBMETADATA_REGISTRATION_STATUS = "registration_status";

	public long timestamp = 0;
	public String version = "";
	public RegistrationStatus registrationStatus = RegistrationStatus.NOT_STARTED;

	public DbMetadata() {
	}

	public DbMetadata(final Cursor results) {
		timestamp = results.getLong(results.getColumnIndex(DbMetadata.FIELD_NAME_DBMETADATA_TIMESTAMP));
		version = results.getString(results.getColumnIndex(DbMetadata.FIELD_NAME_DBMETADATA_VERSION));
		setRegistrationStatus(results.getInt(results
				.getColumnIndex(DbMetadata.FIELD_NAME_DBMETADATA_REGISTRATION_STATUS)));
		setLastUpdated(results.getString(results.getColumnIndex(AbstractDataObj.FIELD_NAME_LAST_UPDATED)));
		setUniqueIdentifier(results.getInt(results.getColumnIndex(BaseColumns._ID)));
	}

	public DbMetadata(final HashMap<String, String> aRow) {
		super(aRow);
		version = aRow.get(DbMetadata.FIELD_NAME_DBMETADATA_VERSION);
		timestamp = Long.parseLong(aRow.get(DbMetadata.FIELD_NAME_DBMETADATA_TIMESTAMP));
		setRegistrationStatus(aRow.get(DbMetadata.FIELD_NAME_DBMETADATA_REGISTRATION_STATUS));
	}

	@Override
	public ContentValues getContentValues() {
		final ContentValues map = super.getContentValues();
		map.put(DbMetadata.FIELD_NAME_DBMETADATA_VERSION, version);
		map.put(DbMetadata.FIELD_NAME_DBMETADATA_VERSION, timestamp);
		map.put(DbMetadata.FIELD_NAME_DBMETADATA_REGISTRATION_STATUS, timestamp);
		return map;
	}

	@Override
	public SparseArray<ArrayList<String>> getFields() {
		final SparseArray<ArrayList<String>> fields = super.getFields();
		int x = fields.size();
		fields.put(x++, getArrayList(DbMetadata.FIELD_NAME_DBMETADATA_VERSION, "VARCHAR(255) NOT NULL"));
		fields.put(x++, getArrayList(DbMetadata.FIELD_NAME_DBMETADATA_TIMESTAMP, "LONG"));
		fields.put(x++, getArrayList(DbMetadata.FIELD_NAME_DBMETADATA_REGISTRATION_STATUS, "LONG"));
		return fields;
	}

	@Override
	public String getSqlUpdateFromV001() {
		return null;
	}

	@Override
	public String getSqlUpdateFromV002() {
		return null;
	}

	@Override
	public String getTableName() {
		return DbMetadata.DATABASE_TABLE_NAME;
	}

	public void setRegistrationStatus(final int newValue) {
		switch (newValue) {
		case 0:
			registrationStatus = RegistrationStatus.NOT_STARTED;
			break;
		case 1:
			registrationStatus = RegistrationStatus.REQUEST_SENT;
			break;
		case 2:
			registrationStatus = RegistrationStatus.REQUEST_ACCEPTED;
			break;
		case 3:
			registrationStatus = RegistrationStatus.REQUEST_DENIED;
			break;
		default:
			registrationStatus = RegistrationStatus.NOT_STARTED;
		}
	}

	public void setRegistrationStatus(final RegistrationStatus aRegStatus) {
		registrationStatus = aRegStatus;
	}

	private void setRegistrationStatus(final String newValueStr) {
		try {
			setRegistrationStatus(Integer.parseInt(newValueStr));
		} catch (final NumberFormatException nfe) {
			Log.wtf(DbMetadata.LOG_TAG, "new Registration status value [" + newValueStr + "] invalid", nfe);
		}
	}

	@Override
	public String toString() {
		return null;
	}

}
