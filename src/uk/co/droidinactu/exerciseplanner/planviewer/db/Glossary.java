package uk.co.droidinactu.exerciseplanner.planviewer.db;

import java.util.Map;

import org.simpleframework.xml.ElementMap;

import uk.co.droidinactu.common.model.AbstractDataObject;

public class Glossary extends AbstractDataObject {

	@ElementMap(entry = "term", key = "key", attribute = true, inline = true)
	private Map<String, String> terms;

	public Glossary() {
	}
}