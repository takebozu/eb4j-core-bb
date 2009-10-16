package fuku.eb4j.io;

import java.util.Hashtable;
import java.util.Vector;

import net.cloudhunter.compat.java.lang.Comparable;
import net.cloudhunter.compat.java.util.ArrayList;
import net.cloudhunter.compat.java.util.Collections;
import net.cloudhunter.compat.java.util.List;

/**
 * ハフマン木を作成するために通常の選択ソートを行うと、ループ回数が多すぎてパフォーマンスが悪い。
 * それを改善するために、処理方法を工夫したクラス。
 * 
 * @author Cloud Hunter
 */
public class HuffmanTree {
	/** HuffmanNodeのVector */
	private Vector _nodes = null;
	
	/** ソート（大->小）に並べられたfreqのVector */
	private Vector _sortedFreqs = null;
	
	/** 処理用<freq, そのfreqを持つHuffmanNode群のVector> */
	private Hashtable _fresNodesMap = null;	//
	
	/**
	 * コンストラクタ
	 */
	public HuffmanTree() {
		this(0);
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param initialCapacity 初期サイズ
	 */
	public HuffmanTree(int initialCapacity) {
		_nodes = new Vector(initialCapacity);
		_sortedFreqs = new Vector();
		_fresNodesMap = new Hashtable();
	}

	/**
	 * 対象ノードを追加する。
	 * @param node
	 */
	public void add(HuffmanNode node) {
		addIndex(node);
		addSortedFreqs(node);
	}

	/**
	 * 選択ソートを行う。
	 */
	private void doSelectionSort() {
		for(int i=0; i<_nodes.size(); i++) {
			Integer freqToBePlaced = null;
			Vector list = null;
			while(true) {
				freqToBePlaced = (Integer)_sortedFreqs.elementAt(0);
				list = (Vector)_fresNodesMap.get(freqToBePlaced);
				if(list.size() > 0) {
					break;
				}
				_sortedFreqs.removeElementAt(0);
			}
			
			HuffmanNode currentNode = (HuffmanNode)_nodes.elementAt(i);
			HuffmanNode nextNode = getMinimumIndexNode(list);
			
			if(currentNode != nextNode) {
				//swap
				Collections.swap(_nodes, i, nextNode.getIndex());
				currentNode.setIndex(nextNode.getIndex());
			}
		}
	}
	
	/**
	 * ハフマン木を取得する。
	 * 
	 * @return
	 */
	public HuffmanNode getTree() {
		doSelectionSort();
		
    	List sortedList = new SortedList(_nodes);
        while (sortedList.size() > 1) {
        	int lastIndex = sortedList.size() - 1;
        	HuffmanNode node1 = (HuffmanNode)sortedList.get(lastIndex);
        	sortedList.remove(lastIndex);
        	
        	lastIndex = sortedList.size() - 1;
        	HuffmanNode node2 = (HuffmanNode)sortedList.get(lastIndex);
        	sortedList.remove(lastIndex);
        	
        	sortedList.add(new HuffmanNode(node1, node2));
        }
        return (HuffmanNode)sortedList.get(0);
	}
	
	/**
	 * Vectorの中でindex値が最も小さいHuffmanNodeを取得する
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
	
	/**
	 * ノードをリストに追加する。
	 * @param node
	 */
	private void addIndex(HuffmanNode node) {
		_nodes.addElement(node);
		node.setIndex(_nodes.size() - 1);
	}
	
	/**
	 * ノードのfreqを調べて、ソート済みfreqのリスト（Vector）と<freq, そのfreqを持つnodeのリスト（Vector）>を生成する。
	 * 
	 * @param node
	 */
	private void addSortedFreqs(HuffmanNode node) {
		int freq = node.getFrequency(); 
		
		Vector list = (Vector)_fresNodesMap.get(new Integer(freq));
		if(list == null) {	
			//そのfrequencyが最初に現れたとき
			list = new Vector();
			_fresNodesMap.put(new Integer(freq), list);
			
			//順番を決める
			boolean isInserted = false;
			for(int i=_sortedFreqs.size()-1; i>=0; i--) {
				Integer elem = (Integer)_sortedFreqs.elementAt(i);
				if(elem.intValue() > freq) {
					_sortedFreqs.insertElementAt(new Integer(freq), i+1);
					isInserted = true;
					break;
				}
			}
			if(!isInserted) {
				_sortedFreqs.insertElementAt(new Integer(freq), 0);
			}
		}
		list.addElement(node);
	}
	
    /**
     * 降順ソート（大->小）されたArrayList。
     * 同じ値の場合、先に入った方がindexの若い方にある。
     */
    private static class SortedList extends ArrayList {
    	public SortedList(Vector v) {
    		_vector = v;
    	}
    	
    	public boolean add(Object o) {
    		for(int i=_vector.size()-1; i>=0; i--) {
    			if( ((Comparable)_vector.elementAt(i)).compareTo(o) >= 0) {
    				_vector.insertElementAt(o, i+1);
    				return true;
    			}
    		}
    		_vector.insertElementAt(o, 0);
    		return true;
    	}
    }
}
