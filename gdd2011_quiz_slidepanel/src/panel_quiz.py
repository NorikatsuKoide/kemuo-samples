#! -*- coding: utf-8 -*-
#!/usr/bin/python
'''
Created on 2011/08/31

@author: yohpapa
'''
import sys
from panel_engine import PanelEngine

class MovingLimit:
	def __init__(self, left, right, up, down):
		self.up	= up
		self.down	= down
		self.left	= left
		self.right	= right
		
	def is_over(self):
		return self.up <= 0 or self.down <= 0 or self.left <= 0 or self.right <= 0

# 移動制限値を取得する
def get_move_limit(row):
	limits = row.split(' ')
	return MovingLimit(
				int(limits[0]),
				int(limits[1]),
				int(limits[2]),
				int(limits[3]))

# ボードの数を取得する
def get_num_board(row):
	return int(row)

# 問題を解く
def solve_exercises(line, limits):
	info	= line.split(',')
	solver = PanelEngine(int(info[0]), int(info[1]), info[2])
	solver.solve(limits)
	return str(solver)

def main():
	
	if len(sys.argv) != 2:
		return

	file_name = sys.argv[1]
	file = None
	try:
		# ファイルから問題の条件などを取得
		file = open(file_name, "r")
		move_limit	= get_move_limit(file.readline())
		num_board	= get_num_board(file.readline())
		
		print 'Start solving ' + str(num_board) + ' problems'
		
		# 問題を解くループ
		for row in file:
			solve = solve_exercises(row, move_limit)
			if move_limit.is_over():
				break
			
			print solve
		
		print 'Solving ' + str(num_board) + ' problems finished'
		
	finally:
		if file != None:
			file.close()
	
if __name__ == '__main__':
	main()