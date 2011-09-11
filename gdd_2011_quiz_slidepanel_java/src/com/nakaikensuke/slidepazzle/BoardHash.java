package com.nakaikensuke.slidepazzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BoardHash {

	private HashMap<Integer, List<Board>> _hash = new HashMap<Integer, List<Board>>();
	private int _averageCost = 0;
	
	public void put(Board board) {
		int key = getHashCode(board);
		
		if(!_hash.containsKey(key)) {
			List<Board> list = new ArrayList<Board>();
			list.add(board);
			_hash.put(key, list);
		} else {
			List<Board> list = _hash.get(key);
			list.add(board);
		}
		
		/*if(_averageCost == 0) {
			_averageCost = board.getCost();
		} else {
			_averageCost = (_averageCost + board.getCost()) / 2;
		}*/
		
		int buffer = 0;
		int num = 0;
		Set<Integer> keys = _hash.keySet();
		Iterator<Integer> iterator = keys.iterator();
		do {
			key = iterator.next();
			List<Board> list = _hash.get(key);
			for(Board item : list) {
				num ++;
				buffer += item.getCost();
			}
		} while(iterator.hasNext());
		
		_averageCost = buffer / num;
	}
	
	public boolean contains(Board board) {
		int key = getHashCode(board);
		if(!_hash.containsKey(key))
			return false;
		
		List<Board> list = _hash.get(key);
		for(Board buf : list) {
			if(buf.compareTo(board) == 0)
				return true;
		}
		return false;
	}
	
	private int getHashCode(Board board) {
		return board.toString().hashCode();
	}
	
	public Board get(Board board) {
		int key = getHashCode(board);
		if(!_hash.containsKey(key))
			return null;
		
		List<Board> list = _hash.get(key);
		for(Board buf : list) {
			if(buf.compareTo(board) == 0)
				return buf;
		}
		return null;
	}
	
	public int getAverageCost() {
		return _averageCost;
	}
}
