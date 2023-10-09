package fr.uge.set;

import java.util.Objects;

public final class HashTableSet<E> {

	static private final record Entry<E>(Entry<E> next, E data) {
		public Entry {
			Objects.requireNonNull(data);
		}
	}
	
	
	
}
