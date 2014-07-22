package uk.co.droidinactu.exerciseplanner.planviewer.db;

public enum WorkoutType {
	RUN, SWIM, CYCLE, CYCLE_INDOOR, BRICK, WEIGHTS, XTRAIN, REST, RACE, RACE_TRI;

	public static WorkoutType getFromString(final String typeStr) {
		if (typeStr.equalsIgnoreCase(SWIM.toString())) {
			return SWIM;
		} else if (typeStr.equalsIgnoreCase(CYCLE.toString())) {
			return CYCLE;
		} else if (typeStr.equalsIgnoreCase(CYCLE_INDOOR.toString())) {
			return CYCLE_INDOOR;
		} else if (typeStr.equalsIgnoreCase(BRICK.toString())) {
			return BRICK;
		} else if (typeStr.equalsIgnoreCase(WEIGHTS.toString())) {
			return WEIGHTS;
		} else if (typeStr.equalsIgnoreCase(XTRAIN.toString())) {
			return XTRAIN;
		} else if (typeStr.equalsIgnoreCase(REST.toString())) {
			return REST;
		} else if (typeStr.equalsIgnoreCase(RACE.toString())) {
			return RACE;
		} else if (typeStr.equalsIgnoreCase(RACE_TRI.toString())) { return RACE_TRI; }
		return RUN;
	}

	@Override
	public String toString() {
		switch (this) {
		case RUN:
			return "Run";
		case SWIM:
			return "Swim";
		case CYCLE:
			return "Cycle";
		case CYCLE_INDOOR:
			return "Spinning";
		case BRICK:
			return "Brick";
		case WEIGHTS:
			return "Weights";
		case XTRAIN:
			return "Cross Train";
		case REST:
			return "Rest";
		case RACE:
			return "Race";
		case RACE_TRI:
			return "Triathlon";
		default:
			throw new IllegalArgumentException();
		}
	}
}
