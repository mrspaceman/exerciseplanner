package uk.co.droidinactu.exerciseplanner.planviewer;

public enum EffortLevel {
	REST("Rest"), GENTLE("Gentle"), EASY("Easy"), FARTLEK("Fartlek"), STEADY("Steady"), TEMPO("Tempo"), HARD_FARTLEK("Hard Fartlek"), HILLS(
			"Hills"), QUICK("Quick"), INTERVALS("Intervals"), CROSSTRAIN("CrossTrain"), RACE("Race");

	private String levelStr;

	private EffortLevel(final String value) {
		levelStr = value;
	}

	public void testChange(final String value) {
		levelStr = value;
	}

	@Override
	public String toString() {
		return levelStr;
	}
}
