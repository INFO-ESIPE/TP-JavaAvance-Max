package fr.uge.query;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public sealed interface Query<E> {

	public static <E, T> Query<T> fromList(
			List<E> list, 
			Function<? super E, ? extends Optional<? extends T>> filter) {

		Objects.requireNonNull(list);
		Objects.requireNonNull(filter);

		return new QueryImpl<E, T>(list, filter);
	}
	
	public static <E> Query<E> fromIterable(Iterable<? extends E> iterable) {
		Objects.requireNonNull(iterable);
		var list = new ArrayList<E>();
		return new QueryImpl<E, E>(iterable, Optional::of);
	}
	

	public List<E> toList();
	public Stream<E> toStream();
	public List<E> toLazyList();
	public Query<E> filter(Predicate<E> filter);

	final class QueryImpl<E, T> implements Query<T> {
		private final Iterable<? extends E> elements;
		private final Function<? super E, ? extends Optional<? extends T>> mapper;
		private final Predicate<E> filter;

		private QueryImpl(Iterable<? extends E> elements, Function<? super E, ? extends Optional<? extends T>> mapper) {
			this(elements, mapper, (e) -> true);
		}
		
		private QueryImpl(Iterable<? extends E> elements, Function<? super E, ? extends Optional<? extends T>> mapper, Predicate<E> filter ) {
			Objects.requireNonNull(elements);
			Objects.requireNonNull(mapper);
			Objects.requireNonNull(filter);
			this.elements = elements;
			this.mapper = mapper;
			this.filter = filter;
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
					.flatMap(e -> mapper.apply(e).stream());
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
		public Query<E> filter(Predicate<E> filter) {
			Objects.requireNonNull(filter);
			this.filter = this.filter.and(filter);
			return this;
		}
	}	
}