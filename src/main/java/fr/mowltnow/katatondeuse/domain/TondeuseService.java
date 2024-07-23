package fr.mowltnow.katatondeuse.domain;

import org.springframework.stereotype.Service;

import fr.mowltnow.katatondeuse.domain.Tondeuse.Instruction;
import fr.mowltnow.katatondeuse.domain.Tondeuse.Position;

@Service
public class TondeuseService {

	public Tondeuse executeInstructions(Instruction instruction) {
		Position position = instruction.position();
		Orientation orientation = instruction.orientation();
		String commands = instruction.commands();

		for (char command : commands.toCharArray()) {
			switch (command) {
			case 'A' -> position = moveForward(position, orientation);
			case 'D' -> orientation = turnRight(orientation);
			case 'G' -> orientation = turnLeft(orientation);
			default -> throw new IllegalArgumentException("Invalid command: " + command);
			}
		}
		return new Tondeuse(position, orientation);
	}

	private Position moveForward(Position position, Orientation orientation) {
		return orientation.moveForward(position);
	}

	private Orientation turnRight(Orientation orientation) {
		return switch (orientation) {
		case Orientation.North north -> new Orientation.East();
		case Orientation.East east -> new Orientation.South();
		case Orientation.South south -> new Orientation.West();
		case Orientation.West west -> new Orientation.North();
		};
	}

	private Orientation turnLeft(Orientation orientation) {
		return switch (orientation) {
		case Orientation.North north -> new Orientation.West();
		case Orientation.East east -> new Orientation.North();
		case Orientation.South south -> new Orientation.East();
		case Orientation.West west -> new Orientation.South();
		};
	}
}