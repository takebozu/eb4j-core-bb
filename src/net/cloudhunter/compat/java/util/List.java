package net.cloudhunter.compat.java.util;

public interface List {
	boolean add(Object o);
	void add(int index, Object element);
	int size();
	Object remove(int index);
	Object get(int index);
//	Object[] toArray();
	Object[] toArray(Object[] a);
}
