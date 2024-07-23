package fr.mowltnow.katatondeuse.infrastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import fr.mowltnow.katatondeuse.domain.Orientation;
import fr.mowltnow.katatondeuse.domain.Tondeuse.Instruction;
import fr.mowltnow.katatondeuse.domain.Tondeuse.Position;

@Component
public class InstructionReader implements ItemReader<Instruction> {

	private static final Logger logger = LoggerFactory.getLogger(InstructionReader.class);

	private List<Instruction> instructions;
	private int nextInstructionIndex;

	public InstructionReader(ResourceLoader resourceLoader, String filePath) {
		try {
			Resource resource = resourceLoader.getResource(filePath);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
				String lines = reader.lines().toList().getFirst();
				instructions = parseInstructions(lines);
				nextInstructionIndex = 0;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to read instructions file", e);
		}
	}

	@Override
	public Instruction read() {
		if (nextInstructionIndex < instructions.size()) {
			return instructions.get(nextInstructionIndex++);
		}
		return null;
	}

	private List<Instruction> parseInstructions(String lines) {
		List<Instruction> parsedInstructions = new ArrayList<>();
		logger.info("File content: {}", lines);
		String[] initialPosition = lines.split(" ");
		for (int i = 2; i < initialPosition.length; i += 4) {
			Position position = new Position(Integer.parseInt(initialPosition[i]),
					Integer.parseInt(initialPosition[i + 1]));
			Orientation orientation = switch (initialPosition[i + 2]) {
			case "N" -> new Orientation.North();
			case "E" -> new Orientation.East();
			case "S" -> new Orientation.South();
			case "W" -> new Orientation.West();
			default -> throw new IllegalArgumentException("Invalid orientation: " + initialPosition[i + 2]);
			};
			String commands = initialPosition[i + 3];
			logger.info("command {}", commands);
			parsedInstructions.add(new Instruction(position, orientation, commands));
		}
		return parsedInstructions;
	}
}