package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
  private Node<E> head;
  private Node<E> tail;
  private int size;

  private static class Node<E> {
    public final E data;
    public Node<E> prev;
    public Node<E> next;

    public Node(E data) {
      this(data, null, null);
    }

    public Node(E data, Node<E> prev, Node<E> next) {
      this.data = data;
      this.prev = prev;
      this.next = next;
    }
  }//Node private class

  public LinkedDeque() {
    this.head = null;
    this.tail = null;
    this.size = 0;
  }
  @Override
  public void addFront(E e) {
    if (this.head == null) {
      this.head = this.tail = new Node<>(e, null, null);
    } else {
      this.head = this.head.prev = new Node<>(e, null, head);
    }
    this.size++;
  }

  @Override
  public void addBack(E e) {
    if (this.head == null) {
      this.head = this.tail = new Node<>(e, null, null);
    } else {
      this.tail = this.tail.next = new Node<>(e, tail, null);
    }
    this.size++;
  }

  @Override
  public E removeFront() {
    if (this.head == null){
      return null;
    }//check null case

    if (this.head.equals(this.tail)) {
      E front = this.head.data;
      this.head = this.tail = null;
      this.size--;
      return front;
    } else {
      E front = this.head.data;
      this.head = this.head.next;
      this.head.prev = null;
      this.size--;
      return front;
    }

  }

  @Override
  public E removeBack() {
    if (this.head == null) {
      return null;
    }
    if (this.head.equals(this.tail)) {
      E front = head.data;
      this.head = this.tail = null;
      this.size--;
      return front;
    } else {
      E back = tail.data;
      this.tail = this.tail.prev;
      this.tail.next = null;
      this.size--;
      return back;
    }
  }

  @Override
  public boolean enqueue(E e) {
    this.addBack(e);
    return true;
  }

  @Override
  public E dequeue() {
    if (!this.isEmpty()) {
      return this.removeFront();
    } else {
      return null;
    }
  }

  @Override
  public boolean push(E e) {
    this.addFront(e);
    return true;
  }//LinkedList is flipped

  @Override
  public E pop() {
   if (!this.isEmpty()) {
     return this.removeFront();
   } else {
     return null;
   }
  }

  @Override
  public E peekFront() {
    if (this.head != null) {
      return this.head.data;
    } else {
      return null;
    }
  }

  @Override
  public E peekBack() {
    if (this.tail != null) {
      return this.tail.data;
    } else {
      return null;
    }
  }

  @Override
  public E peek() {
    return peekFront();
  }

  @Override
  public Iterator<E> iterator() {
    return new LinkedDequeIterator();
  }

  @Override
  public int size() {
    return this.size;
  }

  public String toString() {
    if (this.head == null) {
      return "[]";
    }
    String result = "[" + this.head.data +", ";
    Node<E> curr = this.head;
    for (int i = 0; i < this.size -1; i++) {
        curr= curr.next;
        result += curr.data +", ";
    }
    result = result.substring(0, result.length() -2);
    return result + "]";
  }

  private class LinkedDequeIterator implements Iterator<E> {
    private Node<E> curr;

    public LinkedDequeIterator() {
      this.curr = head;
    }

    @Override
    public boolean hasNext() {
      return this.curr != null;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        return null;
      }
      E toReturn = this.curr.data;
      this.curr = this.curr.next;
      return toReturn;
    }

  }//DequeIterator private class
}
