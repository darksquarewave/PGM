package pgm.graph;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DirectedGraph<T> {

    private final Map<T, Set<? extends T>> adjacencyList;

    public static final class Builder<T> {

        private final Map<T, Set<T>> adj;

        public Builder() {
            adj = new HashMap<>();
        }

        public Builder vertex(final T vertex) {
            if (!adj.containsKey(vertex)) {
                adj.put(vertex, new HashSet<>());
            }

            return this;
        }

        public Builder edge(final T vertex1, final T vertex2) {
            if (!adj.containsKey(vertex1) || !adj.containsKey(vertex2)) {
                throw new IllegalArgumentException("Vertex does not exist");
            }

            Set<T> edges = adj.get(vertex1);
            if (edges.contains(vertex2)) {
                throw new IllegalArgumentException("Edge already exists");
            }

            edges.add(vertex2);

            return this;
        }

        public DirectedGraph<T> build() {
            return new DirectedGraph<>(this);
        }
    }

    private DirectedGraph(final Builder<T> builder) {
        adjacencyList = Collections.unmodifiableMap(builder.adj);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DirectedGraph<?> graph = (DirectedGraph<?>) obj;

        return Objects.equals(adjacencyList, graph.adjacencyList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adjacencyList);
    }
}
