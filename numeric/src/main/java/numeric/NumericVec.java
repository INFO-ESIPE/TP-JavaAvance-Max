package numeric;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NumericVec<T> extends AbstractList<T>{
	private long[] elements;

	private final LongFunction<T> fromLong;
	private final ToLongFunction<T> toLong;

	private int size = 0;

	public static NumericVec<Long> longs(long... longs) {
		Objects.requireNonNull(longs);

		return new NumericVec<Long>(Arrays.copyOf(longs, longs.length), (e) -> (long) e, (e) -> (Long) e);
	}

	public static NumericVec<Integer> ints(int... ints) {
		long[] longs = new long[ints.length];
		for (int i = 0; i < longs.length; i++) {
			longs[i] = (int) ints[i];
		}
		return new NumericVec<Integer>(longs, (e) -> Integer.valueOf(e), (e) -> (int) e);
	}

	public static NumericVec<Double> doubles(double... doubles) {
		long[] longs = new long[doubles.length];
		for (int i = 0; i < longs.length; i++) {
			longs[i] = Double.doubleToRawLongBits(doubles[i]);
		}
		return new NumericVec<Double>(longs, Double::doubleToRawLongBits, Double::longBitsToDouble);
	}

	private NumericVec(long[] longs, ToLongFunction<T> toLong, LongFunction<T> fromLong) {
		if (longs.length < 0)
			throw new IllegalArgumentException();
		Objects.requireNonNull(toLong);
		Objects.requireNonNull(fromLong);

		this.size = longs.length;
		this.toLong = toLong;
		this.fromLong = fromLong;
		this.elements = longs;
	}

	public int size() {
		return size;
	}

	public T get(int index) {
		Objects.checkIndex(index, size());
		return fromLong.apply(elements[index]);
	}

	public boolean add(T element) {
		Objects.requireNonNull(element);
		if (size == elements.length) {
			elements = Arrays.copyOf(elements, (elements.length + 1) * 2);
		}
		elements[size++] = toLong.applyAsLong(element);
		return true;
	}

	private Spliterator<T> fromArray(int start, int end) {
		return new Spliterator<T>() {
			private int i = start;

			@Override
			public Spliterator<T> trySplit() {
				if (size < 1024) {
					return null;
				}
				
				var middle = (i + end) >>> 1;
				if (middle == i) {
					return null;
				}
				var spliterator = fromArray(i, middle);
				i = middle;
				return spliterator;
			}

			@Override
			public boolean tryAdvance(Consumer<? super T> consumer) {
				if (i < end) {
					consumer.accept(fromLong.apply(elements[i++]));
					return true;
				}
				return false;
			}

			@Override
			public int characteristics() {
				var characteristics = SIZED | ORDERED | NONNULL | IMMUTABLE;
				return size < 1024 ?  characteristics : characteristics | SUBSIZED;
			}

			@Override
			public long estimateSize() {
				return end - i;
			}
		};
	}

	public Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
	
	@Override 
	public Spliterator<T> spliterator() {
		return fromArray(0, size);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return true;
	}
}
