package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.uge.slice.Slice.SubArraySlice;

public interface Slice3<E> {

	int size();
	E get(int index);
	Slice3<E> subSlice(int form, int to);
	
	public static void checkFromTo(int from, int to, int size) {
		if(from < 0 || from > size) throw new IndexOutOfBoundsException("'from' value ("+from+") must be > 0 and < " + String.valueOf(size)); 
		if(to < from || to < 0 || to > size) throw new IndexOutOfBoundsException("'to' value ("+to+") mist be >= 0 and < " + String.valueOf(size)); 
	}
	
	
	public static <E> Slice3<E> array(E[] array, int from, int to) {
		Objects.requireNonNull(array);
		Slice3.checkFromTo(from, to, array.length);
		return new Slice3<E>() {
			@Override
			public int size() {
				return to - from; 
			}
			
			@Override
			public E get(int index) {
				if(index < 0 || index >= size()) throw new IndexOutOfBoundsException("Invalid index");
				return array[from+index];
			}
			
			@Override
			public String toString() {
				return Arrays.stream(array, from, to)
					.map(e -> e == null ? "null" : e.toString())
					.collect(Collectors.joining(", ", "[", "]"));
			}
			
			@Override
			public Slice3<E> subSlice(int fromParam, int toParam) {
				Slice3.checkFromTo(fromParam, toParam, size());
				return Slice3.array(array, from + fromParam, from + toParam);
			}
		};
	}

	
	public static <E> Slice3<E> array(E[] array) {
		Objects.requireNonNull(array);
		return new Slice3<E>() {
			@Override
			public int size() {
				return array.length;
			}
			
			@Override
			public E get(int index) {
				if(index < 0 || index > size()) throw new IndexOutOfBoundsException("Index must be between 0 and " + String.valueOf(size()-1));
	 			return array[index];
			}
			
			@Override
			public Slice3<E> subSlice(int form, int to) {
				Slice3.checkFromTo(form, to, array.length);
				return Slice3.array(array, form, to);
			}
			@Override
			public String toString() {
				return Arrays.stream(array)
					.map(e -> e == null ? "null" : e.toString())
					.collect(Collectors.joining(", ", "[", "]"));
			}
		};
	}
}
