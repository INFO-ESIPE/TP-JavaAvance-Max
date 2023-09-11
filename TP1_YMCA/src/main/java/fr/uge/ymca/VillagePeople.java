package fr.uge.ymca;

import java.util.Objects;

public record VillagePeople(String name, Kind kind) implements Resident {
	
	public VillagePeople {
		Objects.requireNonNull(name);
		Objects.requireNonNull(kind);
		if(name.isEmpty()) throw new IllegalArgumentException("The name cannot be empty");
	}
	
	//public int price() { return 100; }
	
	@Override
	public String toString() {
		return name + " ("+kind+")";
	}
}
