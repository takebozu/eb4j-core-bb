package net.cloudhunter.compat.java.util;



public class Collections {
	public static void swap(List list, int i, int j) {
		//force i < j
		if(i == j) {
			return;
		} else if(i > j) {
			int tmp = i;
			i = j;
			j = tmp;
		}
		
		Object v1 = list.get(j);
		list.remove(j);
		list.add(i, v1);
		
		Object v2 = list.get(i+1);
		list.remove(i+1);
		list.add(j, v2);
	}

}
