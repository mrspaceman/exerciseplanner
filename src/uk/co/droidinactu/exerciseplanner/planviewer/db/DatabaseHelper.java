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

import uk.co.droidinactu.common.DroidInActuApplication;
import uk.co.droidinactu.common.db.AbstractDatabaseHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author aspela
 * 
 */
public class DatabaseHelper extends AbstractDatabaseHelper {

	private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

	private boolean firstTimeThrough = true;

	public DatabaseHelper(final Context context) {
		super(context, new DbConstants());
	}

	private void createTables(final SQLiteDatabase db) {
		db.execSQL(new DbMetadata().getSqlCreate());
		db.execSQL(new Day().getSqlCreate());
		db.execSQL(new FAQ().getSqlCreate());
		db.execSQL(new Magazine().getSqlCreate());
		db.execSQL(new Book().getSqlCreate());
		db.execSQL(new Event().getSqlCreate());
		db.execSQL(new EventType().getSqlCreate());
	}

	private void doUpgradeFrom001(final SQLiteDatabase db) {
		db.execSQL(new DbMetadata().getSqlUpdateFromV001());
		db.execSQL(new Member().getSqlUpdateFromV001());
		db.execSQL(new Bike().getSqlUpdateFromV001());
		db.execSQL(new Place().getSqlUpdateFromV001());
		db.execSQL(new PlaceType().getSqlUpdateFromV001());
		db.execSQL(new Event().getSqlUpdateFromV001());
		db.execSQL(new EventType().getSqlUpdateFromV001());
	}

	private void doUpgradeFrom002(final SQLiteDatabase db) {
		db.execSQL(new DbMetadata().getSqlUpdateFromV002());
		db.execSQL(new Member().getSqlUpdateFromV002());
		db.execSQL(new Bike().getSqlUpdateFromV002());
		db.execSQL(new Place().getSqlUpdateFromV002());
		db.execSQL(new PlaceType().getSqlUpdateFromV002());
		db.execSQL(new Event().getSqlUpdateFromV002());
		db.execSQL(new EventType().getSqlUpdateFromV002());
	}

	private void dropTables(final SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + new DbMetadata().getTableName());
		db.execSQL("DROP TABLE IF EXISTS " + new Member().getTableName());
		db.execSQL("DROP TABLE IF EXISTS " + new Bike().getTableName());
		db.execSQL("DROP TABLE IF EXISTS " + new Place().getTableName());
		db.execSQL("DROP TABLE IF EXISTS " + new PlaceType().getTableName());
		db.execSQL("DROP TABLE IF EXISTS " + new Event().getTableName());
		db.execSQL("DROP TABLE IF EXISTS " + new EventType().getTableName());
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		DroidInActuApplication.w("DatabaseHelper::" + "Creating database version " + DbConstants.DATABASE_VERSION);
		createTables(db);
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		super.onOpen(db);
		if (firstTimeThrough) {
			dropTables(db);
			createTables(db);
			firstTimeThrough = false;
		}
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		DroidInActuApplication.w("DatabaseHelper::" + "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		switch (oldVersion) {
		case 1:
			doUpgradeFrom001(db);
		case 2:
			doUpgradeFrom002(db);
		}
		dropTables(db);
		onCreate(db);
	}

}
