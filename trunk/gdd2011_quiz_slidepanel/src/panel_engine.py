#! -*- coding: utf-8 -*-
'''
Created on 2011/08/31

@author: yohpapa
'''
import copy
import math

class PanelEngine:
	def __init__(self, width, height, board):
		self.board = PanelBoard(board, width, height)

	def __str__(self):
		return 'hogehoge'
	
	def solve(self, limits):
		
		# TODO: ここで無限ループする
		
		# まず空白の位置を取得
		blank_position = self.board.get_blank_position()
		print blank_position
		
		# 空白が進める方向の得点を収集
		expected_scores = self.__get_expected_score(blank_position)
		print expected_scores
		
		# これ以上得点を上げることが出来ないかチェックする
		
		# 実際に得点が一番よい方向に空白を移動する
		
		# 制限を使い果たしたかチェックする
		
		# 問題が解けたかチェックする
	
	# 空白を上下左右に動かした時の得点を集計する
	def __get_expected_score(self, current_position):
		
		up_score		= 0
		down_score		= 0
		left_score		= 0
		right_score	= 0
		
		if self.board.can_up(current_position):
			up_score = self.board.get_score_up(current_position)
			
		if self.board.can_down(current_position):
			down_score = self.board.get_score_down(current_position)
			
		if self.board.can_left(current_position):
			left_score = self.board.get_score_left(current_position)
			
		if self.board.can_right(current_position):
			right_score = self.board.get_score_right(current_position)
			
		return (up_score, down_score, left_score, right_score)

class PanelBoard:
	def __init__(self, board, width, height):
		self.board = []
		for i in range(height):
			buf = []
			for j in range(width):
				buf.append(board[j + i * width])
			
			self.board.append(buf)
			
		self.width		= width
		self.height	= height
			
	# 空白の位置を取得する
	def get_blank_position(self):
		for i in range(len(self.board)):
			for j in range(len(self.board[0])):
				if self.board[i][j] == '0':
					return (i, j)
				
		raise Exception('Blank not found')
	
	# 空白を上に動かせるかチェックする
	def can_up(self, current_position):
		if(current_position[0] <= 0):
			return False
		
		if(self.board[current_position[0] - 1][current_position[1]] == '='):
			return False
		
		return True
	
	# 空白を上に動かした場合の得点を経産する
	def get_score_up(self, blank_position):
		buf_board = copy.deepcopy(self.board)
		to_position = (blank_position[0] - 1, blank_position[1])
		
		# 空白を上に移動する
		self.__swap_panel(buf_board, blank_position, to_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)
	
	# ボードのパネルを入れ替える
	def __swap_panel(self, board, from_position, to_position):
		buf_panel = board[to_position[0]][to_position[1]]
		board[to_position[0]][to_position[1]] = board[from_position[0]][from_position[1]]
		board[from_position[0]][from_position[1]] = buf_panel
	
	# 与えられたボードの得点を取得する
	def __get_score(self, board):
		
		score = 0
		
		for i in range(self.height):
			for j in range(self.width):
				if board[i][j] != '0' and board[i][j] != '=':
					goal_position = self.__get_goal_position(board[i][j])
					score += (math.fabs(goal_position[0] - i) + math.fabs(goal_position[1] - j))

		return score
	
	def __get_goal_position(self, panel_value):
		
		value = 0
		if panel_value >= '1' and panel_value <= '9':
			value = int(panel_value) - 1
		elif panel_value >= 'A' and panel_value <= 'Z':
			value = int(panel_value - 'A')
		else:
			raise Exception('Unsupported panel value')
		
		return (value / self.width, value % self.width)
	
	# 空白を下に動かせるかチェックする
	def can_down(self, current_position):
		if(current_position[0] >= self.height - 1):
			return False
		
		if(self.board[current_position[0] + 1][current_position[1]] == '='):
			return False
		
		return True
	
	# 空白を下に動かした場合の得点を経産する
	def get_score_down(self, blank_position):
		buf_board = copy.deepcopy(self.board)
		to_position = (blank_position[0] + 1, blank_position[1])
		
		# 空白を下に移動する
		self.__swap_panel(buf_board, blank_position, to_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)
	
	# 空白を左に動かせるかチェックする
	def can_left(self, current_position):
		if(current_position[1] <= 0):
			return False
		
		if(self.board[current_position[0]][current_position[1] - 1] == '='):
			return False
		
		return True
	
	# 空白を左に動かした場合の得点を経産する
	def get_score_left(self, blank_position):
		buf_board = copy.deepcopy(self.board)
		to_position = (blank_position[0], blank_position[1] - 1)
		
		# 空白を左に移動する
		self.__swap_panel(buf_board, blank_position, to_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)
	
	# 空白を右に動かせるかチェックする
	def can_right(self, current_position):
		if(current_position[1] >= self.width - 1):
			return False
		
		if(self.board[current_position[0]][current_position[1] + 1] == '='):
			return False
		
		return True
	
	# 空白を右に動かした場合の得点を経産する
	def get_score_right(self, blank_position):
		buf_board = copy.deepcopy(self.board)
		to_position = (blank_position[0], blank_position[1] + 1)
		
		# 空白を左に移動する
		self.__swap_panel(buf_board, blank_position, to_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)