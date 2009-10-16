package fuku.eb4j.io;

import java.util.Hashtable;
import java.util.Vector;

import net.cloudhunter.compat.java.util.ArrayList;
import net.cloudhunter.compat.java.util.Collections;

public class SelectionSort extends ArrayList {
	private Vector _sortedFreqs = null;
	private Hashtable _map = null;	//<freq, そのfreqを持つnode>
	
	public SelectionSort() {
		super();
		_sortedFreqs = new Vector();
		_map = new Hashtable();
	}

	public SelectionSort(int size) {
		this();
	}

	public boolean add(Object o) {
		addIndex((HuffmanNode)o);
		addSortedFreqs((HuffmanNode)o);
		return true;
	}
	
	public void doSort() {
		for(int i=0; i<_vector.size(); i++) {
			Integer freqToBePlaced = null;
			Vector list = null;
			while(true) {
				freqToBePlaced = (Integer)_sortedFreqs.elementAt(0);
				list = (Vector)_map.get(freqToBePlaced);
				if(list.size() > 0) {
					break;
				}
				_sortedFreqs.removeElementAt(0);
			}
			
			HuffmanNode currentNode = (HuffmanNode)_vector.elementAt(i);
			HuffmanNode nextNode = getMinimumIndexNode(list);
			
			if(currentNode != nextNode) {
				//swap
				Collections.swap(_vector, i, nextNode.getIndex());
				currentNode.setIndex(nextNode.getIndex());
			}
		}
	}
	
	/**
	 * indexが最も小さいHuffmanNodeを取得する
	 * @param list
	 * @return
	 */
	private HuffmanNode getMinimumIndexNode(Vector list) {
		HuffmanNode node = (HuffmanNode)list.elementAt(0);
		int index = 0;
		for(int i=1; i<list.size(); i++) {
			HuffmanNode loopNode = (HuffmanNode)list.elementAt(i);
			if(node.getIndex() > loopNode.getIndex()) {
				node = loopNode;
				index = i;
			}
		}
		list.removeElementAt(index);
		return node;
	}
	
	private void addIndex(HuffmanNode node) {
		_vector.addElement(node);
		node.setIndex(_vector.size() - 1);
	}
	
	private void addSortedFreqs(HuffmanNode node) {
		int freq = node.getFrequency(); 
		
		Vector list = (Vector)_map.get(new Integer(freq));
		if(list == null) {
			list = new Vector();
			_map.put(new Integer(freq), list);
		}
		list.addElement(node);
		
		for(int i=_sortedFreqs.size()-1; i>=0; i--) {
			Integer elem = (Integer)_sortedFreqs.elementAt(i);
			if(elem.intValue() == freq) {
				return;
			} else if(elem.intValue() > freq) {
				_sortedFreqs.insertElementAt(new Integer(freq), i+1);
				return;
			}
		}
		_sortedFreqs.insertElementAt(new Integer(freq), 0);
	}
}
