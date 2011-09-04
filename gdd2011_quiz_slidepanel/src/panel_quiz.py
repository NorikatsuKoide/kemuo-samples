#! -*- coding: utf-8 -*-
#!/usr/bin/python
'''
Created on 2011/08/31

@author: yohpapa
'''
import sys
from panel_engine import PanelEngine
from logger import Logging
import constants

# 移動量管理
class LimitManager:
	def __init__(self, left, right, up, down):
		self.up	= up
		self.down	= down
		self.left	= left
		self.right	= right
		
	# 移動量を使い果たしたかチェックする
	def isOver(self):
		return self.up <= 0 or self.down <= 0 or self.left <= 0 or self.right <= 0

	# 移動した分制限を更新する (消費する)
	def update(self, dir):
		if dir == constants.DIR_UP:
			self.up -= 1
		elif dir == constants.DIR_DOWN:
			self.down -=1
		elif dir == constants.DIR_LEFT:
			self.left -= 1
		elif dir == constants.DIR_RIGHT:
			self.right -= 1
	
	# 指定方向へロールバックする
	def rollback(self, dir):
		if dir == constants.DIR_UP:
			self.up += 1
		elif dir == constants.DIR_DOWN:
			self.down +=1
		elif dir == constants.DIR_LEFT:
			self.left += 1
		elif dir == constants.DIR_RIGHT:
			self.right += 1
	
# 移動制限値を取得する
def __getMoveLimit(row):
	limits = row.split(' ')
	return LimitManager(
				int(limits[0]),
				int(limits[1]),
				int(limits[2]),
				int(limits[3]))

# ボードの数を取得する
def __getNumBoard(row):
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

	fileName = sys.argv[1]
	file	  = None
	try:
		# ファイルから問題の条件などを取得
		file		= open(fileName, "r")
		limits		= __getMoveLimit(file.readline())
		numBoard	= __getNumBoard(file.readline())
		
		Logging.debug('Start solving ' + str(numBoard) + ' problems')
		
		# 問題を解くループ
		noProblem = 1
		for row in file:
			Logging.debug('Start solving No.%d problem' % noProblem)
			solve = solve_exercises(row, limits)
			if limits.isOver():
				break
			
			# 問題の回答を出力
			Logging.output(solve)
			noProblem += 1
		
		Logging.debug('Solving ' + str(numBoard) + ' problems finished')
		
	finally:
		if file != None:
			file.close()
	
if __name__ == '__main__':
	main()