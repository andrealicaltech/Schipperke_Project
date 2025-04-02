package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
  private E[] data;
  private static final int DEFAULT_CAP = 10;
  private static final int FACTOR = 2;
  private int size;

  public ArrayDeque(int initialCapacity) {
      this.data = (E[]) new Object[initialCapacity];
      this.size = 0;
  }

  public ArrayDeque() {
    this(DEFAULT_CAP);
  }

  private void ensureCapacity(int size) {
    if (this.capacity() <= this.size) {
      E[] newData = (E[]) new Object[this.capacity()*FACTOR];
      for (int i = 0; i< this.size; i++) {
        newData[i] = this.data[i];
      }
      this.data = newData;
    }
  }

  public String toString() {
    if (this.isEmpty()) {
      return "[]";
    }
    String result = "[";
    for (int i = 0; i < this.size; i++) {
      result += this.data[i] +", ";
    }
    result = result.substring(0, result.length() -2);
    return result + "]";
  }

  private int capacity() {
    return this.data.length;
  }


  @Override
  public void addFront(E e) { //shift all back, then add to front
    ensureCapacity(this.size);
    for (int i = this.size; i >0; i--) {
      this.data[i] = this.data[i-1];
    }
    this.data[0] = e;
    this.size++;
  }

  @Override
  public void addBack(E e) {
    ensureCapacity(this.size);
    this.data[this.size] = e;
    this.size++;
  }

  @Override
  public E removeFront() {
    if (!this.isEmpty()) {
      E front = this.data[0];
      for (int i = 1; i < this.size; i++) {
        this.data[i-1] = this.data[i];
      }
      this.data[this.size-1] = null;
      size--;
      return front;
    } else {
      return null;
    }
  }

  @Override
  public E removeBack() {
    if (!this.isEmpty()) {
      E back = this.data[this.size-1];
      this.data[this.size -1] = null;
      this.size--;
      return back;
    } else {
      return null;
    }
  }

  @Override
  public boolean enqueue(E e) {
    addFront(e);
    return true;
  }

  @Override
  public E dequeue() {
    return removeBack();
  }

  @Override
  public boolean push(E e) {
    addBack(e);
    return true;
  }

  @Override
  public E pop() {
    return removeBack();
  }

  @Override
  public E peekFront() {
    if (!this.isEmpty()) {
      return this.data[0];
    } else {
      return null;
    }
  }

  @Override
  public E peekBack() {
    if (this.isEmpty()) {
      return null;
    }
    return this.data[this.size-1];
  }

  @Override
  public E peek() {
    return peekBack();
  }

  @Override
  public Iterator<E> iterator() {
    return new DequeIterator();
  }

  @Override
  public int size() {
    return this.size;
  }

  private class DequeIterator implements Iterator<E> {
    private int idx;
    public DequeIterator() {
      this.idx = 0;
    }

    @Override
    public boolean hasNext() {
      return this.idx < size;
    }

    @Override
    public E next() {
      E toReturn = data[this.idx];
      this.idx++;
      return toReturn;
    }
  }//private class
}

