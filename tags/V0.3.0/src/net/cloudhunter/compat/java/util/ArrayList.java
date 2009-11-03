package net.cloudhunter.compat.java.util;

import java.util.Vector;

public class ArrayList implements List {
	protected Vector _vector = null;
	
	public ArrayList() {
		_vector = new Vector();
	}

	public ArrayList(int initialCapacity) {
		this();
	}
	
	public boolean add(Object o) {
		_vector.addElement(o);
		return true;
	}
	
	public void add(int index, Object element) {
		_vector.insertElementAt(element, index);
	}
	
	public int size() {
		return _vector.size();
	}

	public Object[] toArray(Object[] a) {
		Object[] results = a;
		if(results.length < _vector.size()) {
			results = new Object[_vector.size()];
		}
		
		for(int i = 0; i < _vector.size(); i++) {
			results[i] = _vector.elementAt(i); 
		}
		return results;
	}
	
	public Object remove(int index) {
		Object removedElement = _vector.elementAt(index); 
		_vector.removeElementAt(index);
		return removedElement;
	}
	
	public boolean isEmpty() {
		return _vector.size() == 0;
	}
	
	public void clear() {
		_vector = new Vector();
	}
	
	public Object get(int index) {
		return _vector.elementAt(index);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("{ ");
		for(int i=0; i < _vector.size(); i++) {
			sb.append(_vector.elementAt(i));
			if(i != _vector.size()-1) {
				sb.append(", ");
			}
		}
		sb.append(" }");
		
		return sb.toString();
	}
	
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof ArrayList)) {
			return false;
		} 
		
		List target = (List)obj;
		if(target.size() != this.size()) {
			return false;
		}
		
		for(int i=0; i<target.size(); i++) {
			if(target.get(i) == null) {
				if(this.get(i) != null) {
					return false;
				}
			} else {
				if(!target.get(i).equals(this.get(i))) {
					return false;
				}
			}
		}
		
		return true;
	}
}
