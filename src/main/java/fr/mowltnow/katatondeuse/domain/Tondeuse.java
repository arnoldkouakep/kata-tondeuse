package fr.mowltnow.katatondeuse.domain;

public class Tondeuse {
	private final Position position;
	private final Orientation orientation;

	public Tondeuse(Position position, Orientation orientation) {
		this.position = position;
		this.orientation = orientation;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public String toString() {
		return position.x + " " + position.y + " " + orientation.getSymbol();
	}

	public record Position(int x, int y) {
	}

	public record Instruction(Position position, Orientation orientation, String commands) {
	}
}