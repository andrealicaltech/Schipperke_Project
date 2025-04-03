package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {

    private static class Node<K,V> {
        private K key;
        private V value;
        private Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }//Node private class

    private Node<K, V> head;
    private int size;

    public MoveToFrontDictionary() {
        this.head = null;
        this.size = 0;

    }

    @Override
    public V remove(K key) {
        V curr_val = get(key);

        if (this.head == null) {
            size = 0;
            return null;
        } else if (curr_val == null){
            return null;
        } else {
            Node<K,V> temp = this.head.next;
            this.head = temp;
            size--;
            return curr_val;
        }
    }

    @Override
    public V put(K key, V value) {
        if (get(key) != null) {
            V old = this.head.value;
            this.head.value = value;
            return old;
        } else {
            Node<K,V> new_node = new Node(key, value);
            new_node.next = this.head;
            this.head = new_node;
            size++;
            return null;
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (this.head == null) {
            return false;
        }

        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        if (this.head == null) {
            return false;
        }

        return this.values().contains(value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public ICollection<K> keys() {
        ArrayDeque<K> adeque = new ArrayDeque<>(size);
        if (this.head != null) {
            Node<K,V> temp = this.head;
            adeque.add(temp.key);
            while (temp.next != null) {
                temp = temp.next;
                adeque.add(temp.key);
            }
        }
        return adeque;
    }

    @Override
    public ICollection<V> values() {
        ArrayDeque<V> adeque = new ArrayDeque<>(size);
        if (this.head != null) {
            Node<K,V> temp = this.head;
            adeque.add(temp.value);
            while (temp.next != null) {
                temp = temp.next;
                adeque.add(temp.value);
            }
        }
        return adeque;
    }

    public V get(K key) {
        V curr_val = null;
        Node<K,V> temp = this.head;

        if (head == null) {
            return curr_val;
        }

        if (temp.key.equals(key)) {
            return temp.value;
        }

        while (temp.next != null) {
            if (temp.next.key.equals(key)) {
                Node<K,V> toFront = temp.next;
                curr_val = toFront.value;
                temp.next = temp.next.next;
                toFront.next = this.head;
                this.head = toFront;
                return curr_val;
            }
            temp = temp.next;
        }

        return curr_val;
    }

    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }

    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }
        return "";
    }
}
