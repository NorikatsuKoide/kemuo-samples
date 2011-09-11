package com.nakaikensuke.slidepazzle;

public class Limit implements Cloneable{
	private int _up    = 0;
	private int _down  = 0;
	private int _left  = 0;
	private int _right = 0;
	
	public Limit(int up, int down, int left, int right) {
		_up = up; _down = down; _left = left; _right = right;
	}
	
	public int getUp()    {return _up;}
	public int getDown()  {return _down;}
	public int getLeft()  {return _left;}
	public int getRight() {return _right;}

	public void setUp(int up)       {_up = up;}
	public void setDown(int down)   {_down = down;}
	public void setLeft(int left)   {_left = left;}
	public void setRight(int right) {_right = right;}
	
	public void update(int dir) {
		switch(dir) {
		case Board.UP:	setUp(getUp() - 1);			break;
		case Board.DOWN:	setDown(getDown() - 1);		break;
		case Board.LEFT:	setLeft(getLeft() - 1);		break;
		case Board.RIGHT:	setRight(getRight() - 1);	break;
		default: break;
		}
	}
	
	public boolean isOver() {
		return _up < 0 || _down < 0 || _left < 0 || _right < 0;
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void update(Limit another) {
		_up    = another.getUp();
		_down  = another.getDown();
		_right = another.getRight();
		_left  = another.getLeft();
	}
}
