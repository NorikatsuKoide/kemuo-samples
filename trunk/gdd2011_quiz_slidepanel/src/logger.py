#! -*- coding: utf-8 -*-
'''
Created on 2011/09/03

@author: yohpapa
'''
import sys

class Logging:
	@classmethod
	def debug(cls, msg):
		sys.stderr.write(msg + '\n')
		
	@classmethod
	def output(cls, msg):
		sys.stdout.write(msg + '\n')
		