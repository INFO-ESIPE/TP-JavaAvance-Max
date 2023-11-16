package fr.uge.query;


import java.util.Iterator;
import java.util.List;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public sealed interface Query<E> {

	public static <E, T> Query<T> fromList(
			List<E> list, 
			Function<? super E, ? extends Optional<? extends T>> mapper) {

		Objects.requireNonNull(list);
		Objects.requireNonNull(mapper);

		return new QueryImpl<E, T>(list, mapper);
	}
	
	public static <E> Query<E> fromIterable(Iterable<? extends E> iterable) {
		Objects.requireNonNull(iterable);
		return new QueryImpl<E, E>(iterable, Optional::of);
	}
	

	public List<E> toList();
	public Stream<E> toStream();
	public List<E> toLazyList();
	public Query<E> filter(Predicate<? super E> filter);
	public <R> Query<R> map(Function<? super E, ? extends R> function);
	public <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator);
	
	final class QueryImpl<E, T> implements Query<T> {
		private final Iterable<? extends E> elements;
		private final Function<? super E, ? extends Optional<? extends T>> mapper;
		private final List<Predicate<? super T>> filters = new ArrayList<>();
		
		
		private QueryImpl(Iterable<? extends E> elements, Function<? super E, ? extends Optional<? extends T>> mapper) {
			Objects.requireNonNull(elements);
			Objects.requireNonNull(mapper);
			this.elements = elements;
			this.mapper = mapper;
		}

		@Override
		public String toString() {
			return toStream()
					.map(e -> e.toString())
					.collect(Collectors.joining(" |> "));
		}

		@Override
		public List<T> toList() {
			return toStream().toList();
		}

		@Override
		public Stream<T> toStream() {
			return StreamSupport.stream(elements.spliterator(), false)
				.flatMap(e -> {				
					var optional = mapper.apply(e);
					if(optional.isPresent() && filters.stream().allMatch(f -> f.test(optional.get()))) {
							return optional.stream();
					}
					return Stream.<T>empty();
				});
		}
		
		

		@Override
		public List<T> toLazyList() {
			return new AbstractList<T>() {
				private final Iterator<? extends E> iterator = elements.iterator();
				private final List<T> list = new ArrayList<>();

				@Override
				public T get(int index) {	
					
				    if (index >= list.size()) {
						while (iterator.hasNext()) {
							mapper.apply(iterator.next())
								.ifPresent(list::add);
							if(index < list.size()) {
								break;
							}
						}
				    }

					Objects.checkIndex(index, list.size());
					return list.get(index);	
				}

				@Override
				public int size() {
					while(iterator.hasNext()) {
						mapper.apply(iterator.next())
							.ifPresent(list::add);
					}
					return list.size();
				}
			};
		}

		@Override
		public Query<T> filter(Predicate<? super T> filter) {
			Objects.requireNonNull(filter);
			filters.add(filter);
			return this;
		}

		@Override
		public <R> Query<R> map(Function<? super T, ? extends R> function) {
			Objects.requireNonNull(function);
			return new QueryImpl<E, R>(elements, mapper.andThen(o -> o.map(function)));
		}

		@Override
		public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator) {
			Objects.requireNonNull(accumulator);
			var result = identity;
			for(var e : toList()) {
				 result = accumulator.apply(result, e);
			}
			return result;
		}
	}	
}