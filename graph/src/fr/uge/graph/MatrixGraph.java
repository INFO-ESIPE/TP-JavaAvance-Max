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
	
	public Iterator<Integer> neighborIterator(int src) {
		var neighbors = new ArrayList<Integer>();

		for(int j  = 0; j < nodeCount; j++) {
			if(getWeight(src, j).isPresent()) {
				neighbors.
			}
			
		}
		
		return new Iterator<Integer>() {
			int currentDst = 0;
			
			
			@Override
			public boolean hasNext() {
				return currentDst < nodeCount;
			}

			@Override
			public Integer next() {
				if(!hasNext() || nodeCount == 0) throw new NoSuchElementException();
				int nowDst = currentDst;
				for(; getWeight(src, nowDst).isEmpty(); nowDst++);

				for(currentDst++; getWeight(src, currentDst).isEmpty(); currentDst++);
				currentDst++;
				return nowDst;
			}
	
		};
	}



}
