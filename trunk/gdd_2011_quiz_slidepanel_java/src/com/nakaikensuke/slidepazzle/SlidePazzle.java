package com.nakaikensuke.slidepazzle;

import java.io.BufferedReader;
import java.io.IOException;

public class SlidePazzle {
	
	private Limit _limit = null;
	private BufferedReader _reader = null;
	
	private BoardQueue _queues[] = new BoardQueue[2];
	private BoardHash  _hashs[]  = new BoardHash[2];
	
	private Board _goalBoard  = null;
	private Board _goalBoard1 = null;
	private Board _goalBoard2 = null;
	
	private int _numSolved = 0;
	
	public SlidePazzle(int up, int down, int left, int right, BufferedReader reader) {
		_limit  = new Limit(up, down, left, right);
		_reader = reader;
	}
	
	public void solve() {
		
		int numProblem = 0;
		
		while(true) {
			
			long msec = System.currentTimeMillis();
			
			System.err.println((numProblem + 1) + "問目計算中");
			numProblem ++;
			
			Limit limits[] = new Limit[2];
			limits[0] = (Limit)_limit.clone();
			limits[1] = (Limit)_limit.clone();
			
			try {
				_goalBoard  = null;
				_goalBoard1 = null;
				_goalBoard2 = null;
				
				String line = _reader.readLine();
				if(line == null)
					break;
				
				String[] items = line.split(",");
				
				for(int i = 0; i < _queues.length; i ++) {
					_queues[i] = new BoardQueue();
				}
				for(int i = 0; i < _hashs.length; i ++) {
					_hashs[i] = new BoardHash();
				}

				Board fromFirst = new Board(
									Integer.parseInt(items[0]),
									Integer.parseInt(items[1]),
									items[2],
									getGoalBoard(items[2]),
									_limit);
				
				Board fromLast = new Board(
									Integer.parseInt(items[0]),
									Integer.parseInt(items[1]),
									getGoalBoard(items[2]),
									items[2],
									_limit);
				
				_queues[0].enqueue(fromFirst);
				_hashs[0].put(fromFirst);
				_queues[1].enqueue(fromLast);
				_hashs[1].put(fromLast);
				
				while(true) {
					int result      = start(_queues[0], _hashs[0], _hashs[1], true, msec);
					boolean isBreak = doAfter(result, true, _limit);
					if(isBreak)
						break;
					
					result  = start(_queues[1], _hashs[1], _hashs[0], false, msec);
					isBreak = doAfter(result, false, _limit);
					if(isBreak)
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.err.println("解いたボードの数: " + _numSolved);
	}
	
	private String getGoalBoard(String origin) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < origin.length() - 1; i ++) {
			char str = origin.charAt(i);
			if(str == '=') {
				builder.append('=');
			} else {
				if(i < 9) {
					builder.append((char)('1' + i));
				} else {
					builder.append((char)('A' + i - 9));
				}
			}
		}
		builder.append('0');
		return builder.toString();
	}
	
	private static final int _DIRS[] = {Board.UP, Board.DOWN, Board.LEFT, Board.RIGHT};
	
	private static final int GOAL_IN  = 0;
	private static final int MEET_IN  = 1;
	private static final int CONTINUE = 2;
	private static final int GIVE_UP  = -1;
	
	private int start(BoardQueue queue, BoardHash myHash, BoardHash otherHash, boolean fromStart, long baseTime) {
		
		long currentTime = System.currentTimeMillis();
		if(currentTime - baseTime > 15L * 60L * 1000L)
			return GIVE_UP;
		
		Board nowBoard = queue.dequeue();
		if(nowBoard == null)
			return GIVE_UP;
		
		// System.err.print("cost: " + nowBoard.getCost() + " ");
		// Board.printBoard(nowBoard);
		
		Board.Position blank = nowBoard.getBlankPosition();
		if(blank == null)
			return GIVE_UP;
		
		for(int dir : _DIRS) {
			Board newBoard = nowBoard.move(dir, blank);
			if(newBoard == null)
				continue;

			if(myHash.contains(newBoard))
				continue;
			
			Limit limit = newBoard.getLimit();
			if(limit.isOver())
				continue;
			
			// if(nowBoard.getCost() < newBoard.getCost())
			// if(nowBoard.getCost() + 1 < newBoard.getCost())
			// if(myHash.getAverageCost() < newBoard.getCost())
			if(queue.getAverageCost() < newBoard.getCost())
				continue;
			
			newBoard.setBackBoard(nowBoard);
			
			if(newBoard.isGoal()) {
				_goalBoard = newBoard;
				return GOAL_IN;
			}
			
			if(otherHash.contains(newBoard)) {
				if(fromStart) {
					_goalBoard1 = newBoard;
					_goalBoard2 = otherHash.get(newBoard);
				} else {
					_goalBoard1 = otherHash.get(newBoard);
					_goalBoard2 = newBoard;
				}
				return MEET_IN;
			}
			
			queue.enqueue(newBoard);
			myHash.put(newBoard);
		}
		
		return CONTINUE;
	}
	
	private boolean doAfter(int result, boolean isFirst, Limit master) {
		switch(result) {
		case GOAL_IN:
			master.update(_goalBoard.getLimit());
			Board.printResult(_goalBoard, isFirst);
			_numSolved ++;
			break;
			
		case MEET_IN:
			Limit newLimit = (Limit)bondLimit(
										_goalBoard1.getLimit(),
										_goalBoard2.getLimit(),
										master);
			if(newLimit.isOver()) {
				System.out.println();
				return false;
			} else {
				_numSolved ++;
				master.update(newLimit);
				Board.printResult(_goalBoard1, _goalBoard2);
			}
			break;
			
		case CONTINUE:
			return false;
			
		case GIVE_UP:
			System.out.println();
			break;
			
		default:
			break;
		}
		
		return true;
	}
	
	private Limit bondLimit(Limit fromStart, Limit fromGaol, Limit master) {
		int dU = master.getUp()    - fromGaol.getUp();
		int dD = master.getDown()  - fromGaol.getDown();
		int dL = master.getLeft()  - fromGaol.getLeft();
		int dR = master.getRight() - fromGaol.getRight();
		
		fromStart.setUp(fromStart.getUp()       - dD);
		fromStart.setDown(fromStart.getDown()   - dU);
		fromStart.setRight(fromStart.getRight() - dL);
		fromStart.setLeft(fromStart.getLeft()   - dR);
		
		return fromStart;
	}
}
