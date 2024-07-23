package fr.mowltnow.katatondeuse.domain;

import fr.mowltnow.katatondeuse.domain.Tondeuse.Position;

public class Pelouse {
	private final int width;
	private final int height;

	public Pelouse(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public boolean isInside(Position position) {
		return position.x() >= 0 && position.x() <= width && position.y() >= 0 && position.y() <= height;
	}
}
