package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed interface Slice<E> permits Slice.ArraySlice<E>, Slice.SubArraySlice<E> {

	int size();
	E get(int index);
	Slice<E> subSlice(int form, int to);
	
	
	public static <E> Slice<E> array(E[] array, int from, int to) {
		Objects.requireNonNull(array);
		return new SubArraySlice<E>(array, from, to);
	}
	
	final class SubArraySlice<E> implements Slice<E> {
		private final E[] array;
		private final int from;
		private final int to;

		private SubArraySlice(E[] array, int from, int to) {
			Objects.requireNonNull(array);			
			if(from < 0 || from > array.length) throw new IndexOutOfBoundsException("From is not valid");
			if(to < from || to < 0 || to > array.length) throw new IndexOutOfBoundsException("To is not valid");
			
			this.array = array;	
			this.from = from;
			this.to = to;
		}
		
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
		public Slice<E> subSlice(int from, int to) {
			if(from < 0 || from > size()) throw new IndexOutOfBoundsException("From is not valid");
			if(to < from || to < 0 || to > size()) throw new IndexOutOfBoundsException("To is not valid");
			return new SubArraySlice<E>(array, this.from + from, this.from + to);
		}
		
	}
	
	public static <E> Slice<E> array(E[] array) {
		Objects.requireNonNull(array);
		return new ArraySlice<E>(array);
	}
	
	final class ArraySlice<E> implements Slice<E> {
		private final E[] array;
		
		private ArraySlice(E[] array) {
			Objects.requireNonNull(array);
			this.array = array;	
		}
		
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
		public Slice<E> subSlice(int from, int to) {
			return new SubArraySlice<E>(array, from, to);
		}
		
		@Override
		public String toString() {
			return Arrays.stream(array)
				.map(e -> e == null ? "null" : e.toString())
				.collect(Collectors.joining(", ", "[", "]"));
		}
	}
}
