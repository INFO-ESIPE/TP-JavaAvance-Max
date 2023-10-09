package fr.uge.set;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public final class HashTableSet<E> {

	private final static int DEFAULT_CAPACITY = 16;
	private int capacity = DEFAULT_CAPACITY;
	
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
		capacity *= 2;
   
    	@SuppressWarnings("unchecked")
		Entry<E>[] newEntries = new Entry[capacity];
		forEach(element -> {
			
			int index = hash(element);
			var entry = entries[index];
	        while (entry != null) {
	            if (entry.data.equals(element)) {
	                return;
	            }
	            entry = entry.next();
	        }
		});
		
		entries = newEntries;
	}

	public void add(E element) {
		Objects.requireNonNull(element);
        //if(size()+1 >= capacity/2) {
        	//increaseCapacity();
        //}
        
		int index = hash(element);
        var entry = entries[index];
        while (entry != null) {
            if (entry.data.equals(element)) {
                return;
            }
            entry = entry.next();
        }
        entries[index] = new Entry<>(entries[index], element);
        
	}
	
	public int size() {
        int size = 0;
        for (Entry<E> entry : entries) {
            while (entry != null) {
                size++;
                entry = entry.next();
            }
        }
        return size;
    }
	
	public void forEach(Consumer<E> action) {
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

	
}
