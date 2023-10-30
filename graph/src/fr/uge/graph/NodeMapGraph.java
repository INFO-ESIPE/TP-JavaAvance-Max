package fr.uge.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

final class NodeMapGraph<T> implements Graph<T> {
	private final Map<Integer, Map<Integer, T>> map;
	

	public NodeMapGraph(int nodeCount) {
		map = new HashMap<>(nodeCount);
	}

	@Override
	public int nodeCount() {
		return map.size();
	}

	@Override
	public void addEdge(int src, int dst, T weight) {
		Objects.requireNonNull(weight);
		Objects.checkIndex(src, nodeCount());
		Objects.checkIndex(dst, nodeCount());
		map.computeIfAbsent(src, k -> new HashMap<>()).put(dst, weight);
	}

	@Override
	public Optional<T> getWeight(int src, int dst) {
		Objects.checkIndex(src, nodeCount());
		Objects.checkIndex(dst, nodeCount());
		return Optional.ofNullable(map.getOrDefault(src, Collections.emptyMap()).get(dst));
	}

	@Override
	public Iterator<Integer> neighborIterator(int src) {
		return null;
	}
	
}
