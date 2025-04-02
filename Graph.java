package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IGraph;
import edu.caltech.cs2.interfaces.ISet;

public class Graph<V, E> implements IGraph<V, E> {
    public ChainingHashDictionary<V, ChainingHashDictionary<V, E>> graph;

    public Graph() {
        this.graph = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    @Override
    public boolean addVertex(V vertex) {
        if (graph.containsKey(vertex)) {
            return false;
        }

        graph.put(vertex, new ChainingHashDictionary<>(MoveToFrontDictionary::new));
        return true;
    }

    @Override
    public boolean addEdge(V src, V dest, E e) {
        if (!graph.containsKey(src) || !graph.containsKey(dest)) {
            throw new IllegalArgumentException("Invalid src or dest vertex");
        }//invalid vertexes
        if (graph.get(src).containsKey(dest) && graph.get(src).get(dest) != null) {
            graph.get(src).put(dest, e);
            return false;
        }

        graph.get(src).put(dest, e);
        return true;
    }

    @Override
    public boolean addUndirectedEdge(V n1, V n2, E e) {
        if (addEdge(n2, n1, e) && addEdge(n1, n2, e)) {
            return true;
        }

        graph.get(n1).put(n2, e);
        graph.get(n2).put(n1, e);
        return false;
    }

    @Override
    public boolean removeEdge(V src, V dest) {
        if (graph.containsKey(src)) {
            if (graph.get(src).get(dest) == null) {
                graph.get(src).remove(dest);
                return false;
            }
            graph.get(src).remove(dest);
            return true;
        } else {
            throw new IllegalArgumentException("Invalid src or dest vertex");
        }
    }

    @Override
    public ISet<V> vertices() {
        return graph.keySet();
    }

    @Override
    public E adjacent(V i, V j) {
        if (!graph.containsKey(i) || !graph.containsKey(j)) {
            throw new IllegalArgumentException("Invalid src or dest vertex");
        }

        return graph.get(i).get(j); //return null if this is not there
    }

    @Override
    public ISet<V> neighbors(V vertex) {
        if (!graph.containsKey(vertex) || graph.get(vertex) == null) {
            throw new IllegalArgumentException("Invalid src or dest vertex");
        }
        ISet<V> set = new ChainingHashSet<>();

        for (V cand: graph.get(vertex)) {
            if (graph.get(vertex).get(cand) != null) {
                set.add(cand);
            }
        }

        return set;
    }
}