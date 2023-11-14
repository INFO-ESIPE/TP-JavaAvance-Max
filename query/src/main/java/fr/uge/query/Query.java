package fr.uge.query;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public sealed interface Query<E> {

	public static <E, T> Query<T> fromList(
			List<E> list, 
			Function<? super E, ? extends Optional<? extends T>> filter) {

		Objects.requireNonNull(list);
		Objects.requireNonNull(filter);

		return new QueryImpl<E, T>(list, filter);
	}

	public List<E> toList();
	public Stream<E> toStream();
	public List<E> toLazyList();

	final class QueryImpl<E, T> implements Query<T> {
		private final List<E> elements;
		private final Function<? super E, ? extends Optional<? extends T>> filter;

		private QueryImpl(List<E> elements, Function<? super E, ? extends Optional<? extends T>> filter) {
			Objects.requireNonNull(elements);
			Objects.requireNonNull(filter);
			
			this.elements = Collections.unmodifiableList(elements);
			this.filter = filter;
		}

		@Override
		public String toString() {
			return elements.stream()
					.filter(e -> e != null) 
					.flatMap(e -> filter.apply(e).stream())
					.map(e -> e.toString())
					.collect(Collectors.joining(" |> "));
		}

		@Override
		public List<T> toList() {
			var list = new ArrayList<T>();
			elements.forEach(e -> filter.apply(e).ifPresent(list::add));
			return Collections.unmodifiableList(list);
		}

		@Override
		public Stream<T> toStream() {
			return elements.stream().flatMap(e -> filter.apply(e).stream());
		}

		@Override
		public List<T> toLazyList() {
			return new AbstractList<T>() {
				private final Iterator<E> iterator = elements.iterator();
				private final List<T> list = new ArrayList<>();

				@Override
				public T get(int index) {	
				    if (index >= list.size()) {
						while (iterator.hasNext()) {
							filter.apply(iterator.next())
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
						filter.apply(iterator.next())
						.ifPresent(list::add);
					}
					
					return list.size();
				}
			};
		}
	}	
}