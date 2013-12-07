package com.hqkang.ChatRobot.Sequence;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class MulVHashMap extends HashMap<String, LinkedList<String>>{
		/**
	 * 
	 */
	private static final long serialVersionUID = 4229841108216199158L;

	public MulVHashMap() {
		
	}
		public void putAdd(String sr,String s){
			if(!super.containsKey(sr)){
				super.put(sr, new LinkedList<String>());
			}
				super.get(sr).add(s);
		}
		
		




		public void partRemove(String key) {
			List<String> list = new LinkedList<String>();
			if(!super.isEmpty()) {
				Set<String> part = super.keySet();
				if(part!=null) {
					for(String s:part) {
						if(s.contains(key)) {
							list.add(s);
						}
					}
				}
				
			}
			if(!list.isEmpty()) {
				for(String s:list) {
					super.remove(s);
				}
			}

		}

}
