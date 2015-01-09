package ch.hevs.aislab.magpie.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MultiMap<K,V> {

	private Map<K,ArrayList<V>> multimap;
	
	public MultiMap() {
		multimap = new HashMap<K,ArrayList<V>>();
	}
	
	public void put(K key, V value) {
		if (!multimap.containsKey(key)) {
			ArrayList<V> newListValues = new ArrayList<V>();
			newListValues.add(value);
			multimap.put(key, newListValues);
		} else {
			ArrayList<V> oldListValues = multimap.get(key);
			oldListValues.add(value);
			multimap.put(key, oldListValues);
		}
	}
	
	public ArrayList<V> getValues(K key) {
		return multimap.get(key);
	}
}
