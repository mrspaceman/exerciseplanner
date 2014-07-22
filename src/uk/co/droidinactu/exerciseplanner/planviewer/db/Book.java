package uk.co.droidinactu.exerciseplanner.planviewer.db;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public final class Book extends Reference {
	public static final String DATABASE_TABLE_NAME = "motorbike";

	@Attribute(name = "isbn", required = false)
	public String isbn = "";

	public Book() {
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