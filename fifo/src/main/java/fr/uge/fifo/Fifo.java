package fr.uge.fifo;

import java.util.Arrays;
import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class Fifo<E> extends AbstractQueue<E>  implements Iterable<E>, Queue<E>{
	private final static int DEFAULT_CAPACITY = 16;
	private int capacity;
	private int size = 0;
	private int tail = 0;
	private int head = 0;
	private Object[] elements;

	public Fifo(int capacity) {
		if (capacity < 1)
			throw new IllegalArgumentException("The capacity must be > 0");
		this.capacity = capacity;
		elements = new Object[capacity];
	}

	public Fifo() {
		this(DEFAULT_CAPACITY);
	}

	private void resize() {
		var newElements = new Object[capacity * 2];

		if (head < tail) {
			// Si l'index head est inférieur à l'index head, on ajoute
			for (int i = 0; i < elements.length; i++) {
				newElements[i] = elements[i];
			}
		} else {
			// Sinon, on ajoute les éléments de head à la fin du tableau
			for (int i = head; i < elements.length; i++) {
				newElements[i - head] = elements[i];
			}
			// Et on ajoute les éléments du début du tableau à tail
			for (int i = 0; i < tail; i++) {
				newElements[capacity - head + i] = elements[i];
			}
		}

		elements = newElements;
		head = 0;
		tail = size;
		capacity *= 2;
	}

	public boolean offer(E element) {
		Objects.requireNonNull(element);
		// if(size >= capacity) throw new IllegalStateException("The FIFO is full !");
		if (size * 2 >= capacity) {
			resize();
		}

		elements[tail] = element;
		tail++;
		if (tail >= capacity) {
			tail = 0;
		}
		size++;
		return true;
	}

	@SuppressWarnings("unchecked")
	public E poll() {
		if (size <= 0)
			return null;
		var element = elements[head];
		elements[head] = null; // Set to null to avoid memory leak
		head++;
		if (head >= capacity) {
			head = 0;
		}
		size--;
		return (E) element;
	}

	@SuppressWarnings("unchecked")
	public E peek() {
		if (size <= 0)
			return null;
		return (E) elements[head];

	}

	public int size() {
		return size;
	}

	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int offset = 0;

			@Override
			public boolean hasNext() {
				return size > offset;
			}

			@SuppressWarnings("unchecked")
			@Override
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				int index = (head + offset) % capacity;

				offset++;
				return (E) elements[index];
			}
		};
	}
	

	@Override
	public String toString() {
		return Arrays.stream(elements).filter(Objects::nonNull).sorted(Comparator.comparingInt(o -> {
			for (int i = head; i != tail; i++) {
				if (i == size)
					i = 0;
				if (elements[i].equals(o))
					return 1;
			}
			return -1;
		})).map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
	}

}
