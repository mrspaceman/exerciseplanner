package uk.co.droidinactu.exerciseplanner.planviewer.db;

import org.simpleframework.xml.Root;

@Root
public final class Website extends Reference {
	public static final String DATABASE_TABLE_NAME = "website";

	public Website() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.co.droidinactu.common.model.AbstractDataObj#getTableName()
	 */
	@Override
	public String getTableName() {
		return DATABASE_TABLE_NAME;
	}
}