package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import uk.co.droidinactu.common.model.AbstractDataObj;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.SparseArray;

@Root
public abstract class Reference extends AbstractDataObj {

	protected static String FIELD_NAME_TITLE = "title";
	protected static String FIELD_NAME_TYPE = "type";
	protected static String FIELD_NAME_WEBSITE = "website";

	@Attribute(name = "title", required = false)
	public String title = "";

	@Attribute(name = "type")
	public String type = "";

	@Attribute(name = "website", required = false)
	public String website = "";

	/**
	 * Default Constructor.
	 */
	public Reference() {
	}

	/**
	 * Constructor used for creating an Animal from a Database Row.
	 * 
	 * @param arow
	 */
	public Reference(final Cursor results) {
		super(results);
		title = results.getString(results.getColumnIndex(FIELD_NAME_TITLE));
		type = results.getString(results.getColumnIndex(FIELD_NAME_TYPE));
		website = results.getString(results.getColumnIndex(FIELD_NAME_WEBSITE));
	}

	@Override
	public ContentValues getContentValues() {
		final ContentValues cv = super.getContentValues();
		cv.put(FIELD_NAME_TITLE, title);
		cv.put(FIELD_NAME_TYPE, type);
		cv.put(FIELD_NAME_WEBSITE, website);
		return cv;
	}

	/**
	 * return the field descriptions that make up a row in this table.
	 */
	@Override
	public SparseArray<ArrayList<String>> getFields() {
		final SparseArray<ArrayList<String>> fldList = super.getFields();
		int x = fldList.size();
		fldList.put(x++, getArrayList(FIELD_NAME_TITLE, "VARCHAR(75) not null"));
		fldList.put(x++, getArrayList(FIELD_NAME_TYPE, "VARCHAR(75)"));
		fldList.put(x++, getArrayList(FIELD_NAME_WEBSITE, "VARCHAR(150)"));

		// this.dumpFields(fields);
		return fldList;
	}

	@Override
	public String getSqlUpdateFromV001() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSqlUpdateFromV002() {
		// TODO Auto-generated method stub
		return null;
	}

}
