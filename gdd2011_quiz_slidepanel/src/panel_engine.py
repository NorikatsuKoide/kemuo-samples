#! -*- coding: utf-8 -*-
'''
Created on 2011/08/31

@author: yohpapa
'''
import copy
import math
# from logger import Logging
import constants

class PanelEngine:
	def __init__(self, width, height, board):
		self.board		= PanelBoard(board, width, height)
		self.history	= HistoryManager()

	def __str__(self):
		return str(self.history)
	
	# 問題を解く！
	def solve(self, limits):
		
		backLimits = copy.deepcopy(limits)
		prevScore  = -1
		prevDir    = constants.DIR_INVALID
		
		# ここで無限ループする
		while True:
			
			# まず空白の位置を取得
			blankPos = self.board.getBlankPosition()
			
			# 空白が進める方向の得点を収集
			expectedScores = self.__getExpectedScore(blankPos, prevDir)
			
			# 一番高得点の移動を取得する
			scoreInfo = self.__getHighScore(expectedScores)
			if scoreInfo[0] == constants.DIR_INVALID:
				# 進むべき方向がなくなったら一つ戻って再開
				prevDir = self.history.backTrack(blankPos, self.board, limits)
				if prevDir == -2:
					# Logging.debug('1.これ以上戻ることが出来ない！')
					return
				depth = self.history.getDepth()
				if depth == 1:
					prevScore = -1
				else:
					prevScore	= self.board.getScore()
				continue
			
			# これ以上得点を上げることが出来ないかチェックする
			if prevScore != -1 and prevScore < scoreInfo[1]:
				# 一つ戻って出直す
				prevDir = self.history.backTrack(blankPos, self.board, limits)
				if prevDir == -2:
					# Logging.debug('2.これ以上戻ることが出来ない！')
					return
				depth = self.history.getDepth()
				if depth == 1:
					prevScore = -1
				else:
					prevScore	= self.board.getScore()
				continue
			
			# 実際に得点が一番よい方向に空白を移動する
			self.history.proceed(blankPos, scoreInfo[0], self.board)
			
			# 制限を使い果たしたかチェックする
			consumed = self.__updateLimits(scoreInfo[0], limits)
			if consumed == True:
				limits = copy.deepcopy(backLimits)
				# Logging.debug('移動量を使い果たしてしまった！')
				return
			
			#Logging.debug(
			#		'階層: ' + str(self.history.getDepth()) + ' ' +
			#		'得点: ' + str(scoreInfo[1]) + ' ' +
			#		'方向: ' + str(scoreInfo[0]) + ' ' +
			#		'制限: ' + str(limits.up) + ' ' + str(limits.down) + ' ' + str(limits.left) + ' ' + str(limits.right))
			
			# 問題が解けたかチェックする
			if scoreInfo[1] == 0:
				return
			
			# 次の移動で比較するために今回のスコアをとっておく
			prevDir   = scoreInfo[0]
			prevScore = scoreInfo[1]
	
	# 空白を上下左右に動かした時の得点を集計する
	def __getExpectedScore(self, blankPos, prevDir):
		
		up		= -1
		down	= -1
		left	= -1
		right	= -1
		
		if prevDir != 1 and self.board.canUp(blankPos) and self.history.canProceed(0):
			up = self.board.getScoreUp(blankPos)
			
		if prevDir != 0 and self.board.canDown(blankPos) and self.history.canProceed(1):
			down = self.board.getScoreDown(blankPos)
			
		if prevDir != 3 and self.board.canLeft(blankPos) and self.history.canProceed(2):
			left = self.board.getScoreLeft(blankPos)
			
		if prevDir != 2 and self.board.canRight(blankPos) and self.history.canProceed(3):
			right = self.board.getScoreRight(blankPos)
			
		return (up, down, left, right)
	
	# 4方向の内最も得点の高い方向とその得点を取得する
	def __getHighScore(self, scores):
		
		highIndex = -1
		highScore = -1
		for i in range(len(scores)):
			if scores[i] >= 0 and (highScore < 0 or highScore > scores[i]):
				highScore = scores[i]
				highIndex = i

		return (highIndex, highScore)
	
	# ただ一つしか道がないかチェックする
	def __isOneWay(self, scores):
		isWay = False
		for i in range(len(scores)):
			if scores[i] != -1:
				if isWay:
					return False
				isWay = True
		return True
	
	# 移動制限を更新して、使い切っていないか確認する
	def __updateLimits(self, dir, limits):
		limits.update(dir)
		return limits.isOver()
	
# 履歴管理クラス
class HistoryManager:
	def __init__(self):
		self.history = []
		self.history.append(
						# historyに保存する情報は以下の通り
						# (進んだ方向, [まだ上に進んでいないか, 下, 左, 右])
						(constants.DIR_INVALID, [False, False, False, False]))
		
	def __str__(self):
		str = ''
		for item in self.history:
			str += self.__translate(item[0])
			
		return str
		
	# 方向を文字列に変換する
	def __translate(self, dir):
		if dir == constants.DIR_UP:
			return 'U'
		elif dir == constants.DIR_DOWN:
			return 'D'
		elif dir == constants.DIR_LEFT:
			return 'L'
		elif dir == constants.DIR_RIGHT:
			return 'R'
		else:
			return ''
	
	# 指定された方向に空白を進める
	def proceed(self, pos, dir, board):
		self.history.append((dir, [False, False, False, False]))
		board.movePanel(pos, dir)
		
	# 一つ空白を戻す
	# 戻す際、移動量の制限も戻す
	# @return 戻した時の直前の移動方向
	def backTrack(self, pos, board, limits):
		size = len(self.history)
		if size <= 1:
			return -2
		
		# Logging.debug("戻る！")
		
		incorrect = self.history.pop(len(self.history) - 1)[0]
		self.history[len(self.history) - 1][1][incorrect] = True
		
		reverseDirs = [1, 0, 3, 2]
		board.movePanel(pos, reverseDirs[incorrect])
		limits.rollback(incorrect)
		return self.history[len(self.history) - 1][0]
	
	# 指定方向に進めるかチェックする
	# 以前に進んだけれど、戻ってきた方向は進めないものとする (進んでも仕方がない)
	def canProceed(self, direction):
		if len(self.history) == 0:
			return True
		return not self.history[len(self.history) - 1][1][direction]
	
	# 進んだ階層を取得する	
	def getDepth(self):
		return len(self.history)
	
# パネルボードを管理するクラス
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
	
	# 現状のボードの得点を計算する
	def getScore(self):
		return self.__getScore(self.board)
	
	# 空白の位置を取得する
	def getBlankPosition(self):
		for i in range(len(self.board)):
			for j in range(len(self.board[0])):
				if self.board[i][j] == '0':
					return (i, j)
				
		raise Exception('空白が見つかりません！')
	
	# 空白を上に動かせるかチェックする
	def canUp(self, pos):
		if(pos[0] <= 0):
			return False
		
		if(self.board[pos[0] - 1][pos[1]] == '='):
			return False
		
		return True
	
	# 空白を上に動かした場合の得点を経産する
	def getScoreUp(self, pos):

		# 空白を上に移動する
		buf = copy.deepcopy(self.board)
		self.__moveUp(buf, pos)
		
		# この時のスコアを計算する
		return self.__getScore(buf)
	
	# 空白を上に移動する
	def __moveUp(self, board, fromPos):
		toPos = (fromPos[0] - 1, fromPos[1])
		self.__swapPanel(board, fromPos, toPos)
		
	# ボードのパネルを入れ替える
	def __swapPanel(self, board, fromPos, toPos):
		buffer = board[toPos[0]][toPos[1]]
		board[toPos[0]][toPos[1]] = board[fromPos[0]][fromPos[1]]
		board[fromPos[0]][fromPos[1]] = buffer
	
	# 与えられたボードの得点を取得する
	def __getScore(self, board):
		
		score = 0
		for i in range(self.height):
			for j in range(self.width):
				if board[i][j] != '0' and board[i][j] != '=':
					goalPos = self.__getGoalPos(board[i][j])
					score += int((math.fabs(goalPos[0] - i) + math.fabs(goalPos[1] - j)))

		return score
	
	# 与えられたパネルが本来あるべき位置を取得する
	def __getGoalPos(self, panelValue):
		
		value = 0
		intValue = ord(panelValue)
		if intValue >= ord('1') and intValue <= ord('9'):
			value = intValue - ord('1')
		elif intValue >= ord('A') and intValue <= ord('Z'):
			value = intValue - ord('A') + 10 - 1
		else:
			raise Exception('未対応パネルが見つかってしまった！')
		
		return (value / self.width, value % self.width)
	
	# 空白を下に動かせるかチェックする
	def canDown(self, pos):
		if(pos[0] >= self.height - 1):
			return False
		
		if(self.board[pos[0] + 1][pos[1]] == '='):
			return False
		
		return True
	
	# 空白を下に動かした場合の得点を経産する
	def getScoreDown(self, pos):
		
		# 空白を下に移動する
		buffer = copy.deepcopy(self.board)
		self.__moveDown(buffer, pos)
		
		# この時のスコアを計算する
		return self.__getScore(buffer)

	# 空白を下に移動する
	def __moveDown(self, board, fromPos):
		toPos = (fromPos[0] + 1, fromPos[1])
		self.__swapPanel(board, fromPos, toPos)
	
	# 空白を左に動かせるかチェックする
	def canLeft(self, pos):
		if(pos[1] <= 0):
			return False
		
		if(self.board[pos[0]][pos[1] - 1] == '='):
			return False
		
		return True
	
	# 空白を左に動かした場合の得点を経産する
	def getScoreLeft(self, pos):

		# 空白を左に移動する
		buffer = copy.deepcopy(self.board)
		self.__moveLeft(buffer, pos)
		
		# この時のスコアを計算する
		return self.__getScore(buffer)
	
	# 空白を左に移動する
	def __moveLeft(self, board, fromPos):
		toPos = (fromPos[0], fromPos[1] - 1)
		self.__swapPanel(board, fromPos, toPos)
		
	# 空白を右に動かせるかチェックする
	def canRight(self, pos):
		if(pos[1] >= self.width - 1):
			return False
		
		if(self.board[pos[0]][pos[1] + 1] == '='):
			return False
		
		return True
	
	# 空白を右に動かした場合の得点を経産する
	def getScoreRight(self, pos):

		# 空白を右に移動する
		buffer = copy.deepcopy(self.board)
		self.__moveRight(buffer, pos)
		
		# この時のスコアを計算する
		return self.__getScore(buffer)
	
	# 空白を右に移動する
	def __moveRight(self, board, from_pos):
		to_pos = (from_pos[0], from_pos[1] + 1)
		self.__swapPanel(board, from_pos, to_pos)
		
	# 実際にパネルを動かす
	def movePanel(self, blankPos, dir):
		if dir == constants.DIR_UP:
			self.__moveUp(self.board, blankPos)
		elif dir == constants.DIR_DOWN:
			self.__moveDown(self.board, blankPos)
		elif dir == constants.DIR_LEFT:
			self.__moveLeft(self.board, blankPos)
		else:
			self.__moveRight(self.board, blankPos)
	
	
	
