package fr.uge.graph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SortedVec<E> {
	
	private final E[] array;
	private final Comparator<? super E> comparator;
	
	private SortedVec(E[] array, Comparator<? super E> comparator) {
		this.array = array;
		this.comparator = comparator;
	}

	public int size() {
		return array.length;
	}
	
	public E get(int index) {
		return array[index];
	}
	
	public boolean isIn(E element) {
		return Arrays.binarySearch(array, element, comparator) >= 0; 
	}
	
	static void checkSortedStrings(String[] array) {
		for(int i = 0; i < array.length - 1; i++) {
			if(array[i].compareTo(array[i + 1]) > 0) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public SortedVec<String> ofSortedStrings(List<String> list) {
		Objects.requireNonNull(list);
		var array = list.toArray(String[]::new);
		Arrays.stream(array).forEach(Objects::requireNonNull);
		checkSortedStrings(array);
		return new SortedVec<>(array, String::compareTo);
	}
	
	
	static <T> void checkSorted(T[] array,  Comparator<? super T> comparator) {
		for(int i = 0; i < array.length - 1; i++) {
			if(comparator.compare(array[1], array[i + 1])> 0) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	
	public static <T> SortedVec<T> ofSorted(List<? extends T> list, Comparator<? super T> comparator) {
		Objects.requireNonNull(list);
		Objects.requireNonNull(comparator);
		@SuppressWarnings("unchecked")
		var array = (T[]) list.toArray();
		Arrays.stream(array).forEach(Objects::requireNonNull);
		checkSorted(array, comparator);
		return new SortedVec<>(array, comparator);
	}
	
	

	
	

	
}
