package uk.co.droidinactu.exerciseplanner.planviewer.db;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public final class Magazine extends Reference {
	public static final String DATABASE_TABLE_NAME = "magazine";

	@Attribute(name = "editionMonth", required = false)
	public String editionMonth = "";

	@Attribute(name = "editionNbr", required = false)
	public String editionNbr = "";

	@Attribute(name = "editionYear", required = false)
	public String editionYear = "";

	public Magazine() {
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