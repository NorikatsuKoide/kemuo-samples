package com.nakaikensuke.slidepazzle;

public class Board implements Comparable<Board>{
	
	public static final int UP      = 0;
	public static final int DOWN    = 1;
	public static final int LEFT    = 2;
	public static final int RIGHT   = 3;
	public static final int INVALID = -1;
	
	public class Position {
		private int _x;
		private int _y;
		public Position(int x, int y) {
			_x = x;
			_y = y;
		}
		public int getX() {return _x;}
		public int getY() {return _y;}
	}
	
	private int _width;
	private int _height;
	private byte[][] _board;
	private byte[][] _goal;
	private int _cost;
	private Board _backBoard = null;
	private int _prevDir = INVALID;
	private Limit _limit = null;
	
	public Board(int width, int height, String board, String goal, Limit limit) {
		_width  = width;
		_height = height;
		_board  = new byte[height][width];
		_goal   = new byte[height][width];
		
		byte[] bytes1 = board.getBytes();
		byte[] bytes2 = goal.getBytes();
		int counter = 0;
		for(int y = 0; y < height; y ++) {
			for(int x = 0; x < width; x ++) {
				_board[y][x] = bytes1[counter];
				_goal [y][x] = bytes2[counter];
				counter ++;
			}
		}
		
		_cost = calcCost(_board);
		_limit = limit;
	}
	
	private Board(int width, int height, byte[][] board, byte[][] goal, int dir, Limit limit) {
		_width   = width;
		_height  = height;
		_board   = new byte[height][width];
		_goal    = goal;
		_prevDir = dir;
		
		for(int y = 0; y < height; y ++) {
			for(int x = 0; x < width; x ++) {
				_board[y][x] = board[y][x];
			}
		}
		
		_cost = calcCost(_board);
		_limit = limit;
	}
	
	private int calcCost(byte[][] board) {
		int cost = 0;
		for(int y = 0; y < _height; y ++) {
			for(int x = 0; x < _width; x ++) {
				char item = (char)board[y][x];
				if(item == '0' || item == '=')
					continue;

				Position pos = getGoalPosition(item);
				cost += Math.abs(pos.getX() - x) + Math.abs(pos.getY() - y);
			}
		}
		return cost;
	}
	
	private Position getGoalPosition(char item) {
		
		for(int y = 0; y < _height; y ++) {
			for(int x = 0; x < _width; x ++) {
				if(item == _goal[y][x])
					return new Position(x, y);
			}
		}
		
		return null;
	}
	
	public int getCost() {
		return _cost;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int y = 0; y < _height; y ++) {
			for(int x = 0; x < _width; x ++) {
				builder.append((char)_board[y][x]);
			}
		}
		return builder.toString();
	}
	
	public boolean isGoal() {
		return _cost == 0;
	}
	
	public Board move(int dir, Position blank) {
		Board board = null;
		switch(dir) {
		case UP:
			board = moveUp(blank);
			break;
			
		case DOWN:
			board = moveDown(blank);
			break;
			
		case LEFT:
			board = moveLeft(blank);
			break;
			
		case RIGHT:
			board = moveRight(blank);
			break;
			
		default:
			break;
		}
		return board;
	}
	
	public Position getBlankPosition() {
		for(int y = 0; y < _height; y ++) {
			for(int x = 0; x < _width; x ++) {
				if(_board[y][x] == '0')
					return new Position(x, y);
			}
		}
		return null;
	}
	
	private Board moveUp(Position blank) {
		if(blank.getY() <= 0)
			return null;
		
		if(_board[blank.getY() - 1][blank.getX()] == '=')
			return null;
		
		Limit limit = (Limit)_limit.clone();
		limit.update(UP);
		
		Board board = new Board(_width, _height, _board, _goal, UP, limit);
		return board.swap(blank, new Position(blank.getX(), blank.getY() - 1));
	}
	
	private Board moveDown(Position blank) {
		if(blank.getY() >= _height - 1)
			return null;
		
		if(_board[blank.getY() + 1][blank.getX()] == '=')
			return null;
		
		Limit limit = (Limit)_limit.clone();
		limit.update(DOWN);
		
		Board board = new Board(_width, _height, _board, _goal, DOWN, limit);
		return board.swap(blank, new Position(blank.getX(), blank.getY() + 1));
	}
	
	private Board moveLeft(Position blank) {
		if(blank.getX() <= 0)
			return null;
		
		if(_board[blank.getY()][blank.getX() - 1] == '=')
			return null;
		
		Limit limit = (Limit)_limit.clone();
		limit.update(LEFT);
		
		Board board = new Board(_width, _height, _board, _goal, LEFT, limit);
		return board.swap(blank, new Position(blank.getX() - 1, blank.getY()));
	}
	
	private Board moveRight(Position blank) {
		if(blank.getX() >= _width - 1)
			return null;
		
		if(_board[blank.getY()][blank.getX() + 1] == '=')
			return null;
		
		Limit limit = (Limit)_limit.clone();
		limit.update(RIGHT);
		
		Board board = new Board(_width, _height, _board, _goal, RIGHT, limit);
		return board.swap(blank, new Position(blank.getX() + 1, blank.getY()));
	}
	
	private Board swap(Position pos1, Position pos2) {
		
		byte buffer = _board[pos1.getY()][pos1.getX()];
		_board[pos1.getY()][pos1.getX()] = _board[pos2.getY()][pos2.getX()];
		_board[pos2.getY()][pos2.getX()] = buffer;
		
		_cost = calcCost(_board);
		
		return this;
	}
	
	public void setBackBoard(Board board) {
		_backBoard = board;
	}
	
	public Board getBackBoard() {
		return _backBoard;
	}

	public byte get(int x, int y) {
		return _board[y][x];
	}
	
	@Override
	public int compareTo(Board board) {
		for(int y = 0; y < _height; y ++) {
			for(int x = 0; x < _width; x ++) {
				if(_board[y][x] == board.get(x, y))
					continue;
				
				return _board[y][x] - board.get(x, y);
			}
		}
		return 0;
	}
	
	public char getDirection() {
		char dir = ' ';
		switch(_prevDir) {
		case UP:		dir = 'U';		break;
		case DOWN:		dir = 'D';		break;
		case LEFT:		dir = 'L';		break;
		case RIGHT:	dir = 'R';		break;
		default:		dir = '\n';	break;
		}
		
		return dir;
	}
	
	public Limit getLimit() {
		return _limit;
	}
	
	public static void printResult(Board board, boolean reverse) {
		StringBuilder builder = new StringBuilder();
		while(board != null) {
			builder.append(board.getDirection());
			board = board.getBackBoard();
		}
		
		if(reverse) {
			builder.reverse();
		}
		
		System.out.print(builder.toString() + "\n");
	}
	
	public static void printResult(Board fromStart, Board fromLast) {
		StringBuilder builder = new StringBuilder();
		while(fromStart != null) {
			if(fromStart.getBackBoard() == null)
				break;
			
			builder.append(fromStart.getDirection());
			fromStart = fromStart.getBackBoard();
		}
		
		builder.reverse();
		
		while(fromLast != null) {
			if(fromLast.getBackBoard() == null)
				break;
			
			builder.append(reverseDir(fromLast.getDirection()));
			fromLast = fromLast.getBackBoard();
		}
		
		System.out.print(builder.toString() + "\n");
	}
	
	private static char reverseDir(char dir) {
		switch(dir) {
		case 'U': return 'D';
		case 'D': return 'U';
		case 'L': return 'R';
		case 'R': return 'L';
		default:
			return '0';
		}
	}
	
	public int getHeight() {
		return _height;
	}
	
	public int getWidth() {
		return _width;
	}
	
	public static void printBoard(Board board) {
		for(int y = 0; y < board.getHeight(); y ++) {
			for(int x = 0; x < board.getWidth(); x ++) {
				System.err.print((char)board.get(x, y));
			}
			System.err.println();
		}
	}
}
