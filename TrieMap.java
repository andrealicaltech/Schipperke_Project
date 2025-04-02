package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;

    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = null;
        this.collector = collector;
        this.size = 0;
    }


    @Override
    public boolean isPrefix(K key) {
        if (this.root == null){
            return false;
        }
        Iterator itr = key.iterator();
        TrieNode curr = root;
        while (itr.hasNext()) {
            A next = (A) itr.next();
            if (curr == null) {
                return false;
            }
            if (curr.pointers.containsKey(next)) {
                curr = (TrieNode) curr.pointers.get(next);
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public ICollection<V> getCompletions(K prefix) {
        ArrayDeque<V> completions = new ArrayDeque<>();
        if (this.root == null){
            return completions;
        }
        Iterator itr = prefix.iterator();   // so if "ad" is the key, iterator has ['a', 'd']
        TrieNode curr = root;
        while (itr.hasNext()) {
            A next = (A) itr.next();
            if (curr == null) {
                return completions;
            }
            if (curr.pointers.containsKey(next)) {
                curr = (TrieNode) curr.pointers.get(next);
            }
            else {
                return completions;
            }
        }
        trav_values(curr, completions);
        return completions;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public V get(K key) {
        V val = null;
        Iterator itr = key.iterator();
        TrieNode curr = root;
        while (itr.hasNext()) {
            A next = (A) itr.next();
            if (curr == null) {
                return val;
            }
            if (curr.pointers.containsKey(next)) {
                curr = (TrieNode) curr.pointers.get(next);
            }
            else {
                return val;
            }
        }
        if (curr == null) return null;
        return (V) curr.value;
    }

    @Override
    public V remove(K key) {
        // Sure! Here is a TrieMap implementation in Java. It combines a Trie (Prefix Tree) with a HashMap to efficiently store and retrieve key-value pairs, supporting fast prefix-based searches.
        Iterator it = key.iterator();

        ArrayDeque<V> ans = new ArrayDeque<>();
        if (trav_remove(root, it, ans)) {   // if the private function returns true
            root = null;
        }
        if (ans.peek() != null) {   // if we actually removed a node that used to exist
            size--;
        }
        return ans.peek();
    }

    private boolean trav_remove(TrieNode<A, V> curr, Iterator<A> itr, ArrayDeque<V> val) {
        if (curr == null) return true;  // key: a d a
        if (!itr.hasNext()) {   // if we've gone through every letter in the key
            val.add((V) curr.value);    // save what we're about to remove
            curr.value = null;  // remove the node that corresponds to the last letter in our key
            return (curr.pointers.isEmpty());   // our curr node points to nothing else
        }
        else {
            A next = itr.next();
            TrieNode<A, V> newnode = curr.pointers.get(next);
            if (trav_remove(newnode, itr, val)) {
                curr.pointers.remove(next);
            }
            return (curr.value == null && curr.pointers.isEmpty());

        }
    }

    @Override
    public V put(K key, V value) {
        Iterator itr = key.iterator();
        if (root == null) {
            root = new TrieNode<>();
        }
        TrieNode curr = root;
        while (itr.hasNext()) {
            A next = (A) itr.next();
            if (curr == null) {
                curr = new TrieNode();
                root = curr;
            }
            if (curr.pointers.containsKey(next)) {
                curr = (TrieNode) curr.pointers.get(next);
            }
            else {
                curr.pointers.put(next, new TrieNode<>());
                curr = (TrieNode) curr.pointers.get(next);
            }
        }
        V prev_val = (V) curr.value;
        if (prev_val == null) size++;
        curr.value = value;
        return prev_val;
    }

    @Override
    public boolean containsKey(K key) {
        Iterator itr = key.iterator();
        TrieNode curr = root;
        while (itr.hasNext()) {
            A next = (A) itr.next();
            if (curr == null) {
                return false;
            }
            if (curr.pointers.containsKey(next)) {
                curr = (TrieNode) curr.pointers.get(next);
            }
            else {
                return false;
            }
        }
        if ((V) curr.value == null) return false;
        return true;
    }

    @Override
    public boolean containsValue(V value) {
        return trav_contains(root, value);
    }
    private boolean trav_contains(TrieNode curr, V value) {
        if (curr == null) return false;
        if (curr.value != null) {
            if (curr.value == value) return true;
        }
        boolean yes = false;
        for (var child : curr.pointers.values()) {
            yes = yes || trav_contains((TrieNode) child, value);
        }
        return yes;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public ICollection<K> keys() {
        ArrayDeque<K> keys = new ArrayDeque<>();
        if (this.root == null) {
            return keys;
        }
        trav_keys(this.root, keys, new ArrayDeque<A>());
        return keys;
    }

    private void trav_keys(TrieNode curr, ArrayDeque<K> keys, ArrayDeque<A> acc) {
        if (curr.value != null) {   // ex, "ad" not key, but "adam" is
            keys.add(this.collector.apply(acc));
        }
        for (var child : curr.pointers.keySet()) {  // child is a key, aka the arrow thing
            acc.add((A) child);
            trav_keys((TrieNode) curr.pointers.get((A) child),keys, acc);
            acc.removeBack();
        }
    }

    @Override
    public ICollection<V> values() {
        ArrayDeque<V> values = new ArrayDeque<>();
        trav_values(root, values);
        return values;
    }

    private void trav_values(TrieNode curr, ArrayDeque<V> values) {
        if (curr == null) return;
        if (curr.value != null) values.add((V) curr.value);
        for (var child : curr.pointers.values()) {
            trav_values((TrieNode) child, values);
        }
    }

    @Override
    public Iterator<K> iterator() {
        ArrayDeque<K> keys = (ArrayDeque<K>) keys();
        return keys.iterator();
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    private static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}
