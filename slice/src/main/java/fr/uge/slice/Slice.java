package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed interface Slice<E> permits Slice.ArraySlice<E>, Slice.SubArraySlice<E> {

	int size();
	E get(int index);
	
	final class SubArraySlice<E> implements Slice<E> {
		private final E[] array;

		private SubArraySlice(E[] array, int from, int to) {
			Objects.requireNonNull(array);
			this.array = array;	
		}
		
		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public E get(int index) {
			// TODO Auto-generated method stub
			return null;
		}
		
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
		public String toString() {
			return Arrays.asList(array)
				.stream()
				.map(e -> e == null ? "null" : e.toString())
				.collect(Collectors.joining(", ", "[", "]"));
		}
	}
	
	
	public static <E> Slice<E> array(E[] array) {
		Objects.requireNonNull(array);
		return new ArraySlice<E>(array);
	}
}
