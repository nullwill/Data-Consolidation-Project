package com.yrl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of a sorted list using an array-based approach. This class
 * maintains the elements in sorted order according to a provided comparator.
 * 
 * @param <T> the type of elements in the list
 */
public class SortedArrayList<T> implements Iterable<T> {
	private Object[] elements;
	private int size;
	private Comparator<T> comparator;

	private static final int DEFAULT_CAPACITY = 10;
	private static final int GROW_FACTOR = 2;

	/**
	 * Constructs a new SortedArrayList with the specified comparator.
	 * 
	 * @param comparator the comparator used to determine the order of elements in
	 *                   the list
	 */
	public SortedArrayList(Comparator<T> comparator) {
		this.elements = new Object[DEFAULT_CAPACITY];
		this.size = 0;
		this.comparator = comparator;
	}

	/**
	 * Adds the specified element to the list while maintaining its sorted order.
	 * 
	 * @param element the element to be added
	 */
	public void add(T element) {
		if (size == elements.length) {
			resizeArray();
		}

		int index = findInsertionIndex(element);
		shiftElementsToRight(index);
		elements[index] = element;
		size++;
	}

	/**
	 * Removes the specified element from the list if it exists.
	 * 
	 * @param element the element to be removed
	 * @return true if the element was removed, false otherwise
	 */
	public boolean remove(T element) {
		int index = findElementIndex(element);
		if (index != -1) {
			shiftElementsToLeft(index);
			size--;
			return true;
		}
		return false;
	}

	/**
	 * Returns the element at the specified index in the list.
	 * 
	 * @param index the index of the element to retrieve
	 * @return the element at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
		return (T) elements[index];
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			elements[i] = null;
		}
		size = 0;
	}

	private void resizeArray() {
		int newCapacity = elements.length * GROW_FACTOR;
		Object[] newArray = new Object[newCapacity];
		System.arraycopy(elements, 0, newArray, 0, size);
		elements = newArray;
	}

	@SuppressWarnings("unchecked")
	private int findInsertionIndex(T element) {
		int index = 0;
		while (index < size && comparator.compare((T) elements[index], element) < 0) {
			index++;
		}
		return index;
	}

	private void shiftElementsToRight(int index) {
		System.arraycopy(elements, index, elements, index + 1, size - index);
	}

	private int findElementIndex(T element) {
		for (int i = 0; i < size; i++) {
			if (elements[i].equals(element)) {
				return i;
			}
		}
		return -1;
	}

	private void shiftElementsToLeft(int index) {
		System.arraycopy(elements, index + 1, elements, index, size - index - 1);
		elements[size - 1] = null;
	}

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int currentIndex = 0;

			public boolean hasNext() {
				return currentIndex < size;
			}

			@SuppressWarnings("unchecked")
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return (T) elements[currentIndex++];
			}
		};
	}
}