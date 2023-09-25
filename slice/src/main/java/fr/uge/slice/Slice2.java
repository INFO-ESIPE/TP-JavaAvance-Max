package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.uge.slice.Slice2.ArraySlice.SubArraySlice;

public sealed interface Slice2<E> permits Slice2.ArraySlice<E>, Slice2.ArraySlice<E>.SubArraySlice {

	int size();
	E get(int index);
	//Slice2<E> subSlice(int form, int to);
	
	
	public static <E> Slice2<E> array(E[] array) {
		Objects.requireNonNull(array);
		return new ArraySlice<E>(array);
	}
	
	public static <E> Slice2<E> array(E[] array, int from, int to) {
		Objects.requireNonNull(array);
		return  Slice2.array(array).new SubArraySlice(array, from, to);
	}
	
	final class ArraySlice<E> implements Slice2<E> {
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
			return Arrays.stream(array)
				.map(e -> e == null ? "null" : e.toString())
				.collect(Collectors.joining(", ", "[", "]"));
		}
		
		
		public final class SubArraySlice implements Slice2<E> {
			private final int from;
			private final int to;

			private SubArraySlice(int from, int to) {
				Objects.requireNonNull(array);			
				if(from < 0 || from > array.length) throw new IndexOutOfBoundsException("From is not valid");
				if(to < from || to < 0 || to > array.length) throw new IndexOutOfBoundsException("To is not valid");
				
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

			
		}
	}
}
