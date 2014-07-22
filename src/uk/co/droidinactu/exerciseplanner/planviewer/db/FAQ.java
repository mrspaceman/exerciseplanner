package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.util.Map;

import org.simpleframework.xml.ElementMap;

public class FAQ {
	@ElementMap(entry = "question", key = "key", attribute = true, inline = true)
	private Map<String, String> questions;
	public long help_id = -1;

	public FAQ() {
	}
}
