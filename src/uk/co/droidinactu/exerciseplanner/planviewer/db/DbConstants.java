package uk.co.droidinactu.exerciseplanner.planviewer.db;

import uk.co.droidinactu.common.db.AbstractDbConstants;

public final class DbConstants implements AbstractDbConstants {

	public static final String DATABASE_EXT = ".db";

	public static final String DATABASE_NAME = "ClubMgrDb";

	// any time you make changes to your database objects, you may have to
	// increase the database version
	public static final int DATABASE_VERSION = 1;

	@Override
	public String getDbName() {
		return DATABASE_NAME;
	}

	@Override
	public int getDbVersion() {
		return DATABASE_VERSION;
	}

}
