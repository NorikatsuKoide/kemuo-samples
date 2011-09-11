package com.nakaikensuke.slidepazzle;

import java.util.ArrayList;
import java.util.List;

public class BoardQueue {

	private List<Board> _queue = new ArrayList<Board>();
	private int _averageCost = 0;
	
	public void enqueue(Board board) {
		_queue.add(board);
		
		_averageCost = calcAverage();
	}
	
	private int calcAverage() {
		if(_queue.size() == 0)
			return 0;
		
		int buffer = 0;
		for(Board board : _queue) {
			buffer += board.getCost();
		}
		return buffer / _queue.size();
	}
	
	public Board dequeue() {
		if(_queue.size() <= 0)
			return null;

		Board board = _queue.get(0);
		for(int i = 1; i < _queue.size(); i ++) {
			Board buffer = _queue.get(i);
			if(buffer.getCost() < board.getCost()) {
				board = buffer;
			}
		}
		_averageCost = calcAverage();
		
		_queue.remove(board);
		
		return board;
	}
	
	public Board peek() {
		return _queue.get(0);
	}
	
	public int getAverageCost() {
		return _averageCost;
	}
}
