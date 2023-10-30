package fr.uge.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;


final class MatrixGraph<T> implements Graph<T> {
	private final T[] array;
	private final int nodeCount;
	
	@SuppressWarnings("unchecked")
	public MatrixGraph(int nodeCount) {
		if(nodeCount < 0) { 
			throw new IllegalArgumentException("nodeCount must be positive");
		}
		this.nodeCount = nodeCount;
		array = (T[]) new Object[nodeCount*nodeCount];
		for(int i = 0; i < array.length; i++) {
			array[i] = null;
		}
	}
	
	public int nodeCount() {
		return nodeCount;
	}


	public void addEdge(int src, int dst, T weight) {
		Objects.requireNonNull(weight);
		Objects.checkIndex(src, nodeCount);
		Objects.checkIndex(dst, nodeCount);
		array[nodeCount * src + dst] = weight;
	}
	
	public Optional<T> getWeight(int src, int dst) {
		Objects.checkIndex(src, nodeCount);
		Objects.checkIndex(dst, nodeCount);
		return (array[nodeCount * src + dst] != null) ?
				Optional.of(array[nodeCount * src + dst]) :
				Optional.empty();
	}
	
	private class NeighborIterator implements Iterator<Integer> {
		private int next = -1;
		private int current = -1;
		private final int src;
		
		public NeighborIterator(int src) {
			Objects.checkIndex(src, nodeCount);
			this.src = src;
			findNext();
		}
		
		@Override
		public boolean hasNext() {
			return next != -1;
		}
		
		@Override
		public Integer next() {
		
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			current = next;
			findNext();
			return current;
		}
		
		@Override
		public void remove() {
			if(current == -1) {
				throw new IllegalStateException();
			}
			array[nodeCount * src + current] = null;
			current = -1;
		}
		
		private void findNext() {
			next = -1;
			for(int i = current + 1; i < nodeCount(); i++) {
				if(getWeight(src, i).isPresent()) {
					next = i;
					return;
				}
			}
		}
	}
	
	public Iterator<Integer> neighborIterator(int src) {
		return new NeighborIterator(src);
	}


}
