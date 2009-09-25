package net.cloudhunter.compat.java.util;

import java.util.Hashtable;

public class HashMap implements Map {
	private Hashtable _table = null;
	
	public HashMap() {
		_table = new Hashtable();
	}
	
	public HashMap(int initialCapacity, float loadFactor) {
		_table = new Hashtable(initialCapacity);
	}
	
	public Object get(Object key) {
		return _table.get(key);
	}
	
	public Object put(Object key, Object value) {
		return _table.put(key, value);
	}
	
	public Object remove(Object key) {
		return _table.remove(key);
	}
}
