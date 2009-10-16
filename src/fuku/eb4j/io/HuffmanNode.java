package fuku.eb4j.io;

import net.cloudhunter.bb.EBLogger;
import net.cloudhunter.compat.java.lang.Comparable;
import net.cloudhunter.compat.java.util.ArrayList;
import net.cloudhunter.compat.java.util.Collections;
import net.cloudhunter.compat.java.util.List;
import net.rim.device.api.system.EventLogger;

/**
 * ハフマンノードクラス。
 *
 * @author Hisaya FUKUMOTO
 */
public class HuffmanNode implements Comparable {

    /** EOF葉ノード */
    protected static final int LEAF_EOF = 0;
    /** 8bit葉ノード */
    protected static final int LEAF_8 = 1;
    /** 16bit葉ノード */
    protected static final int LEAF_16 = 2;
    /** 32bit葉ノード */
    protected static final int LEAF_32 = 3;

    /** 葉ノードの種類 */
    private int _leafType = -1;
    /** 値 */
    private long _value = -1L;
    /** 出現頻度値 */
    private int _frequency = 0;
    /** 左子ノード */
    private HuffmanNode _left = null;
    /** 右子ノード */
    private HuffmanNode _right = null;

    /** Vectorに格納したときのインデックス（HuffmanTree作成用） */
    private int index = -1;
    
    public void setIndex(int index) {
    	this.index = index;
    }
    public int getIndex() {
    	return index;
    }
    
    

    /**
     * コンストラクタ。 (葉ノード用)
     *
     * @param value 値
     * @param frequency 出現頻度値
     * @param leafType 葉ノードの種類
     */
    protected HuffmanNode(long value, int frequency, int leafType) {
        _value = value;
        _frequency = frequency;
        _leafType = leafType;
    }

    /**
     * コンストラクタ。 (枝ノード用)
     *
     * @param left 左子ノード
     * @param right 右子ノード
     */
    protected HuffmanNode(HuffmanNode left, HuffmanNode right) {
        _left = left;
        _right = right;
        _frequency = _left.getFrequency() + _right.getFrequency();
    }


    /**
     * 葉ノードの種類を返します。
     *
     * @return 葉ノードの種類
     */
    protected int getLeafType() {
        return _leafType;
    }

    /**
     * 値を返します。
     *
     * @return 値
     */
    protected long getValue() {
        return _value;
    }

    /**
     * 出現頻度値を返します。
     *
     * @return 出現頻度値
     */
    protected int getFrequency() {
        return _frequency;
    }

    /**
     * 左子ノードを返します。
     *
     * @return 左子ノード
     */
    protected HuffmanNode getLeft() {
        return _left;
    }

    /**
     * 右子ノードを返します。
     *
     * @return 右子ノード
     */
    protected HuffmanNode getRight() {
        return _right;
    }

    /**
     * 葉ノードか枝ノードかを判別します。
     *
     * @return 葉ノードであればtrue、枝ノードであればfalse
     */
    protected boolean isLeaf() {
        if (_right == null && _left == null) {
            return true;
        }
        return false;
    }

    /**
     * オブジェクトのハッシュコード値を返します。
     *
     * @return ハッシュコード値
     */
    public int hashCode() {
        return (int)(_value + _frequency);
    }

    /**
     * このオブジェクトとほかのオブジェクトが等しいかどうかを返します。
     *
     * @param obj 比較対象オブジェクト
     * @return 等しい場合はtrue、そうでない場合はfalse
     */
    public boolean equals(Object obj) {
        if (obj instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)obj;
            if (node.getLeafType() == getLeafType()
                && node.getValue() == getValue()
                && node.getFrequency() == getFrequency()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 2つのノードの出現頻度値を比較します。
     *
     * @param node 比較対象のノード
     * @return このノードの頻度値が引数ノードの頻度値と等しい場合0、
     *         このノードの頻度値が引数ノードの頻度値より小さい場合は0より小さい値、
     *         このノードの頻度値が引数ノードの頻度値より大きい場合は0より大きい値
     */
//    @Override
    public int compareTo(Object node) {
        int ret = getFrequency() - ((HuffmanNode)node).getFrequency();
        return ret;
    }
    
    public String toString() {
    	return "freqency=" + _frequency
    		+ "\tvalue=" + _value
    		+ "\tleafType=" + _leafType;
    }

    /**
     * ハフマンツリーを作成します。
     *
     * @param list HuffmanNodeのリスト
     * @return ルートノード
     */
    protected static HuffmanNode makeTree(List list) {	//List<HuffmanNode>
    	EBLogger.log("[S]makeTree", EventLogger.DEBUG_INFO);

//        // ソート (選択ソート：大->小)
//        int size = list.size();
//        for (int i=0; i<size-1; i++) {
//        	HuffmanNode current = (HuffmanNode)list.get(i);
//            int n = i;
//            for (int j=i+1; j<size; j++) {
//            	HuffmanNode tmp = (HuffmanNode)list.get(j);
//                if (current.compareTo(tmp) < 0) {
//                	current = tmp;
//                    n = j;
//                }
//            }
//            if (i != n) {
//                Collections.swap(list, i, n);
//            }
//        }
    	
    	SelectionSort slist = new SelectionSort();
    	for(int i=0; i<list.size(); i++) {
    		slist.add(list.get(i));
    	}
    	slist.doSort();
    	

        // ハフマンツリーの作成
    	List sortedList = new SortedList((ArrayList)slist);
        while (sortedList.size() > 1) {
        	int lastIndex = sortedList.size() - 1;
        	HuffmanNode node1 = (HuffmanNode)sortedList.get(lastIndex);
        	sortedList.remove(lastIndex);
        	
        	lastIndex = sortedList.size() - 1;
        	HuffmanNode node2 = (HuffmanNode)sortedList.get(lastIndex);
        	sortedList.remove(lastIndex);
        	
        	sortedList.add(new HuffmanNode(node1, node2));
        }

        EBLogger.log("[E]makeTree", EventLogger.DEBUG_INFO);
        return (HuffmanNode)sortedList.get(0);
    }

    /**
     * 降順ソート（大->小）されたArrayList。
     * 同じ値の場合、先に入った方がindexの若い方にある。
     */
    private static class SortedList extends ArrayList {
    	public SortedList(ArrayList list) {
    		super(list);
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

// end of HuffmanNode.java
