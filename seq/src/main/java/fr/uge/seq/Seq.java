package fr.uge.seq;

import java.util.List;


import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Seq<E> {
	
	int size();
	
	E get(int index);
	
	<R> Seq<R> map(Function<? super E, ? extends R> mapper);
	
	public static <E> Seq<E> from(List<? extends E> list) {
		Objects.requireNonNull(list);
		return new SeqImpl<E, E>(list, Function.identity());
	}
	
	default Optional<E> findFirst() {
		if(size() == 0) return Optional.empty();
		return Optional.ofNullable(get(0));
	}
	
	Stream<E> stream();


	final class SeqImpl<E, T> implements Seq<E> {
		private final List<T> list;
		private final Function<? super T, ? extends E> mapper;

		
		private SeqImpl(List<? extends T> list, Function<? super T, ? extends E> mapper) {
			Objects.requireNonNull(list);
			Objects.requireNonNull(mapper);
			this.mapper = mapper;
			this.list = List.copyOf(list);	
		}

		@Override
		public int size() {
			return list.size();
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return mapper.apply(list.get(index));
		}
		
		@Override
		public <R> Seq<R> map(Function<? super E, ? extends R> mapper) {
			Objects.requireNonNull(mapper);
			return new SeqImpl<R, T>(list, this.mapper.andThen(mapper));
		}
		
		
		
		@Override
		public Stream<E> stream() {
			var spliterator = spliterator(0, list.size());
			return StreamSupport.stream(spliterator, false);
		}
		
		private Spliterator<E> spliterator(int start, int end) {

			return  new Spliterator<E>() {
				 private int i = start;
				 @Override
				 public Spliterator<E> trySplit() { 
					 var middle = (i + end) >>> 1;
					 if(middle == i) {
						 return null;
					 }
					 var spliterator = spliterator(i, middle);
					 i = middle;
					 return spliterator;
				 }
				 
				 @Override
				 public boolean tryAdvance(Consumer<? super E> consumer) {
					 Objects.requireNonNull(consumer);
					 if (i < end) {
						 consumer.accept(get(i++));
						 return true;
					 }
					 return false;
				 }
				 
				 @Override
				 public int characteristics() { 
					 return ORDERED | IMMUTABLE; 
				 }
				 
				 @Override
				 public long estimateSize() { 
					 return end-i; 
				 }
		 	};
			
		}
		

		
		
		@Override
		public String toString() {
			return list.stream().map(e -> mapper.apply(e))
					.map(Object::toString)
					.collect(Collectors.joining(", ", "<", ">"));
		}

	

	}
	
}
