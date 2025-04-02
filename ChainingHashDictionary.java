package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;
import java.util.function.Supplier;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private int size;
    private IDictionary<K,V>[] table;
    private int[] primes;


    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.chain = chain;
        this.size = 0;
        this.primes = new int[]{11, 23, 47, 97, 181, 367, 733, 1469, 2927, 5851, 11701, 23417, 46831, 93683, 187337, 374719, 718387};

        this.table = new IDictionary[primes[0]];

        for (int i = 0; i < primes[0]; i++) {
            table[i] = chain.get();
        }
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        int index = Math.abs(key.hashCode()) % this.table.length;
        return table[index].get(key);
    }

    @Override
    public V remove(K key) {
        int index = Math.abs(key.hashCode()) % this.table.length;
        V curr_val = table[index].remove(key);
        if (curr_val != null) {
            size--;
        }
        return curr_val;
    }

    @Override
    public V put(K key, V value) {
        if (size > this.table.length * 1.0) {
            int newCap = 0;
            for (int i = 0; i < primes.length; i++) {
                if (this.table.length < primes[i]) {
                    newCap = primes[i];
                    break;
                }
            }

            IDictionary<K,V>[] newDic = new IDictionary[newCap];

            for (int i = 0; i < newCap; i++) {
                newDic[i] = chain.get();
            }

            for (int i = 0; i < this.table.length; i++) {
                for (K k: table[i].keys()) {
                    V val = table[i].get(k);
                    int index = Math.abs(k.hashCode()) % newCap;
                    newDic[index].put(k, val);
                }
            }

            this.table = newDic;
        }

        int index = Math.abs(key.hashCode()) % this.table.length;
        if (!table[index].containsKey(key)) {
            size++;
        }
        return table[index].put(key, value);
    }

    @Override
    public boolean containsKey(K key) {
        int index = Math.abs(key.hashCode()) % this.table.length;
        return table[index].containsKey(key);
    }

    /**
     * @param value
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        for (int i = 0; i < this.table.length; i++) {
            if (table[i].containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public ICollection<K> keys() {
        ArrayDeque<K> adeque = new ArrayDeque<>();
        //for each dictionary, go through each call keySet(), add to adeque
        for (IDictionary<K,V> dic : table) {
            for (K key: dic.keys()) {
                adeque.add(key);
            }
        }
        return adeque;
    }

    @Override
    public ICollection<V> values() {
        ArrayDeque<V> adeque = new ArrayDeque<>();
        //for each map, go through each .values(), add to adeque
        for (IDictionary<K,V> dic : table) {
            for (V value: dic.values()) {
                adeque.add(value);
            }
        }
        return adeque;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
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
