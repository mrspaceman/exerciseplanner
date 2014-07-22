package uk.co.droidinactu.exerciseplanner.planviewer.db;

import org.simpleframework.xml.Element;

import uk.co.droidinactu.common.model.AbstractDataObject;

public class Help extends AbstractDataObject {

	@Element(name = "instructions", required = false)
	private String instructions;

	@Element(name = "glossary", required = false)
	private Glossary glossary;

	@Element(name = "faq", required = false)
	private FAQ faq;

	public Help() {
	}
}
