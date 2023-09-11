package fr.uge.ymca;

import java.util.Objects;

public record Minion(String name) implements Resident{

	public Minion {
		Objects.requireNonNull(name);
		if(name.isEmpty()) throw new IllegalArgumentException("The name cannot be empty");
	}
	
	//public int price() { return 1; }

	@Override
	public String toString() {
		return name + " (MINION)";
	}
}
