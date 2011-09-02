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
		self.result = ''

	def __str__(self):
		return self.result
	
	def solve(self, limits):
		
		# 解けなかった問題はパスするので移動制限を復帰させる
		back_limits = copy.deepcopy(limits)
		
		self.result = ''
		prev_score  = -1
		prev_dir    = -1
		buf_result	= ''
		
		# ここで無限ループする
		while True:
			
			# まず空白の位置を取得
			blank_pos = self.board.get_blank_position()
			
			# 空白が進める方向の得点を収集
			expected_scores = self.__get_expected_score(blank_pos, prev_dir)
			
			# 一番高得点の移動を取得する
			score_info = self.__get_high_score(expected_scores)
			if score_info[0] == -1:
				# どの方向にもいけなくなった
				limits = copy.deepcopy(back_limits)
				return
			
			# これ以上得点を上げることが出来ないかチェックする
			if prev_score != -1 and prev_score <= score_info[1]:
				limits = copy.deepcopy(back_limits)
				return
			
			# 実際に得点が一番よい方向に空白を移動する
			# 同時にどれだけ移動に費やしたかを取得する
			self.board.move_panel(score_info[0], blank_pos)
			
			# 制限を使い果たしたかチェックする
			consumed = self.__update_limits(score_info[0], limits)
			if consumed == True:
				limits = copy.deepcopy(back_limits)
				return
			
			# 問題が解けたかチェックする
			if score_info[1] == 0:
				self.result = buf_result
				return
			
			# 次の移動で比較するために今回のスコアをとっておく
			prev_dir   = score_info[0]
			prev_score = score_info[1]
			
			# 移動方向列の文字列を更新する
			buf_result += self.__get_result_str(score_info[0])
	
	# 空白を上下左右に動かした時の得点を集計する
	def __get_expected_score(self, current_position, prev_dir):
		
		up_score		= -1
		down_score		= -1
		left_score		= -1
		right_score	= -1
		
		if prev_dir != 1 and self.board.can_up(current_position):
			up_score = self.board.get_score_up(current_position)
			
		if prev_dir != 0 and self.board.can_down(current_position):
			down_score = self.board.get_score_down(current_position)
			
		if prev_dir != 3 and self.board.can_left(current_position):
			left_score = self.board.get_score_left(current_position)
			
		if prev_dir != 2 and self.board.can_right(current_position):
			right_score = self.board.get_score_right(current_position)
			
		return (up_score, down_score, left_score, right_score)
	
	# 4方向の内最も得点の高い方向とその得点を取得する
	def __get_high_score(self, scores):
		
		high_index = 0
		high_score = -1
		for i in range(len(scores)):
			if scores[i] >= 0 and (high_score < 0 or high_score > scores[i]):
				high_score = scores[i]
				high_index = i

		return (high_index, high_score)
	
	# 移動制限を更新して、使い切っていないか確認する
	def __update_limits(self, direction, limits):
		if direction == 0:
			limits.up -= 1
		elif direction == 1:
			limits.down -= 1
		elif direction == 2:
			limits.left -= 1
		else:
			limits.right -= 1
			
		return limits.up == 0 or limits.down == 0 or 	limits.left == 0 or limits.right == 0
	
	# 移動方向の文字を取得する
	def __get_result_str(self, direction):
		if direction == 0:
			return 'U'
		elif direction == 1:
			return 'D'
		elif direction == 2:
			return 'L'
		else:
			return 'R'
		
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
	
	# 文字列を返却する
	def __str__(self):
		str = ''
		for i in range(self.height):
			for j in range(self.width):
				str += self.board[j][i]
				
		return str
	
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

		# 空白を上に移動する
		buf_board = copy.deepcopy(self.board)
		self.__move_up(buf_board, blank_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)
	
	# 空白を上に移動する
	def __move_up(self, board, from_pos):
		to_pos = (from_pos[0] - 1, from_pos[1])
		self.__swap_panel(board, from_pos, to_pos)
		
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
					score += int((math.fabs(goal_position[0] - i) + math.fabs(goal_position[1] - j)))

		return score
	
	def __get_goal_position(self, panel_value):
		
		value = 0
		int_value = ord(panel_value)
		if int_value >= ord('1') and int_value <= ord('9'):
			value = int_value - ord('1')
		elif int_value >= ord('A') and int_value <= ord('Z'):
			value = int_value - ord('A')
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
		
		# 空白を下に移動する
		buf_board = copy.deepcopy(self.board)
		self.__move_down(buf_board, blank_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)

	# 空白を下に移動する
	def __move_down(self, board, from_pos):
		to_pos = (from_pos[0] + 1, from_pos[1])
		self.__swap_panel(board, from_pos, to_pos)
	
	# 空白を左に動かせるかチェックする
	def can_left(self, current_position):
		if(current_position[1] <= 0):
			return False
		
		if(self.board[current_position[0]][current_position[1] - 1] == '='):
			return False
		
		return True
	
	# 空白を左に動かした場合の得点を経産する
	def get_score_left(self, blank_position):

		# 空白を左に移動する
		buf_board = copy.deepcopy(self.board)
		self.__move_left(buf_board, blank_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)
	
	# 空白を左に移動する
	def __move_left(self, board, from_pos):
		to_pos = (from_pos[0], from_pos[1] - 1)
		self.__swap_panel(board, from_pos, to_pos)
		
	# 空白を右に動かせるかチェックする
	def can_right(self, current_position):
		if(current_position[1] >= self.width - 1):
			return False
		
		if(self.board[current_position[0]][current_position[1] + 1] == '='):
			return False
		
		return True
	
	# 空白を右に動かした場合の得点を経産する
	def get_score_right(self, blank_position):

		# 空白を右に移動する
		buf_board = copy.deepcopy(self.board)
		self.__move_right(buf_board, blank_position)
		
		# この時のスコアを計算する
		return self.__get_score(buf_board)
	
	# 空白を右に移動する
	def __move_right(self, board, from_pos):
		to_pos = (from_pos[0], from_pos[1] + 1)
		self.__swap_panel(board, from_pos, to_pos)
		
	# 実際にパネルを動かす
	def move_panel(self, direction, blank_pos):
		if direction == 0:
			self.__move_up(self.board, blank_pos)
		elif direction == 1:
			self.__move_down(self.board, blank_pos)
		elif direction == 2:
			self.__move_left(self.board, blank_pos)
		else:
			self.__move_right(self.board, blank_pos)
	
	
	