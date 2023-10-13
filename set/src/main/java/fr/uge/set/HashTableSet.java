package fr.uge.set;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public final class HashTableSet<E> {

	private final static int DEFAULT_CAPACITY = 16;
	private int capacity = DEFAULT_CAPACITY;
	private int size = 0;
	
	@SuppressWarnings("unchecked")
	private Entry<E>[] entries = new Entry[DEFAULT_CAPACITY];
	
	static private final record Entry<E>(Entry<E> next, E data) {
		public Entry {
			Objects.requireNonNull(data);
		}
	}
	
	public HashTableSet() {
		for(int i = 0; i < capacity; i++) {
			entries[i] = null;
		}
	}
	
	private void increaseCapacity() {
		Entry<E>[] newEntries = new Entry[capacity*2];
		
		forEach(element -> {
			int index = hash(element);
			newEntries[index] = entries[index];
		});
		
		capacity *= 2;
		entries = newEntries;
	}

	public void add(E element) {
		Objects.requireNonNull(element);
		//System.out.println("Number of elements :" + size() + " | Capacity :" + capacity);
        if(size >= capacity/2) {
        	increaseCapacity();
        }
        
		int index = hash(element);
        var entry = entries[index];
        while (entry != null) {
            if (entry.data.equals(element)) {
                return;
            }
            entry = entry.next();
        }
        size++;
        entries[index] = new Entry<>(entries[index], element);
	}
	

	public int size() {
		/*
        int size = 0;
        for (Entry<E> entry : entries) {
            while (entry != null) {
                size++;
                entry = entry.next();
            }
        }
        */
        return size;
    }
	public void forEach(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		Arrays.stream(entries).forEach(entry -> {
			while (entry != null) {
				action.accept(entry.data());
				entry = entry.next();
			}
		});
	}
	
	private int hash(E element) {
		return Math.abs(element.hashCode() & capacity-1);
	}

	
	public boolean contains(E element) {
		Objects.requireNonNull(element);
		int index = hash(element);
        var entry = entries[index];
        
        while (entry != null) {

            if (entry.data().equals(element)) {
                return true;
            }
    		//System.out.println("test1" + entry.toString());

            entry = entry.next();
        }
        return false;
	}
	
	public void addAll(HashTableSet<? extends E> hashTableSet) {
		hashTableSet.forEach(this::add);
	}
	

	

	@Override
	public String toString() {
		var sb = new StringBuilder();
		for(int i = 0; i < capacity; i++) {
			sb.append(i + ": ");
			if(entries[i] == null) {
				sb.append("[null]");
			} else {
				for(var entry = entries[i]; entry != null; entry = entry.next()) {
					sb.append(" -> " + entry.toString());
				}
			}
		}
		return sb.toString();
	}

	
}
