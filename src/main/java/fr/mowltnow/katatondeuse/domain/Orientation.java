package fr.mowltnow.katatondeuse.domain;

import fr.mowltnow.katatondeuse.domain.Tondeuse.Position;

public sealed interface Orientation permits Orientation.North, Orientation.East, Orientation.South, Orientation.West {
	Position moveForward(Position position);

	String turnRight();

	String turnLeft();
	
	char getSymbol();

	final class North implements Orientation {
		@Override
		public Position moveForward(Position position) {
			return new Position(position.x(), position.y() + 1);
		}

		@Override
		public String turnRight() {
			return "EAST";
		}

		@Override
		public String turnLeft() {
			return "WEST";
		}

		@Override
		public char getSymbol() {
			return 'N';
		}
	}

	final class East implements Orientation {
		@Override
		public Position moveForward(Position position) {
			return new Position(position.x() + 1, position.y());
		}

		@Override
		public String turnRight() {
			return "SOUTH";
		}

		@Override
		public String turnLeft() {
			return "NORTH";
		}

		@Override
		public char getSymbol() {
			return 'E';
		}
	}

	final class South implements Orientation {
		@Override
		public Position moveForward(Position position) {
			return new Position(position.x(), position.y() - 1);
		}

		@Override
		public String turnRight() {
			return "WEST";
		}

		@Override
		public String turnLeft() {
			return "EAST";
		}

		@Override
		public char getSymbol() {
			return 'S';
		}
	}

	final class West implements Orientation {
		@Override
		public Position moveForward(Position position) {
			return new Position(position.x() - 1, position.y());
		}

		@Override
		public String turnRight() {
			return "NORTH";
		}

		@Override
		public String turnLeft() {
			return "SOUTH";
		}

		@Override
		public char getSymbol() {
			return 'W';
		}
	}
}