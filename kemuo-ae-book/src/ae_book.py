#! -*- coding: utf-8 -*-

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

import os
import time
import datetime

class Book(db.Model):
	title = db.StringProperty()
	author = db.StringProperty()
	copyright_year = db.IntegerProperty()
	author_birthdate = db.DateProperty()

class MainPage(webapp.RequestHandler):

	def get(self):
		self.response.headers['Content-Type'] = 'text/html'
		body = ''
		self.response.out.write('''
		<html>
			<head>
				<title>The Time Is...</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
			<img src="/images/%s/sample.png" />
			%s
			<hr>
			<form action="/register" method="post">
				<table>
				<tr>
					<td class="label">title:</td>
					<td><input type="text" name="title"></td>
				</tr>
				<tr>
					<td class="label">author:</td>
					<td><input type="text" name="author"></td>
				</tr>
				<tr>
					<td class="label">copyright_year:</td>
					<td><input type="number" name="copyright_year"></td>
				</tr>
				<tr>
					<td class="label">author_birthdate:</td>
					<td><input type="date" name="author_birthdate"></td>
				</tr>
				</table>
				<input type="submit" value="OK">
			</form>
			</body>
		</html>
		''' % (os.environ['CURRENT_VERSION_ID'],
				os.environ['CURRENT_VERSION_ID'],
				body))

class Register(webapp.RequestHandler):
	def post(self):
		
		# まずはPOSTのBodyを全て取り出す
		title				= self.request.get('title')
		author				= self.request.get('author')
		copyright_year	= self.request.get('copyright_year')
		author_birthdate	= self.request.get('author_birthdate')
		
		# 全部未入力の場合は何もしないでトップページにリダイレクト
		if not (self.validateInput(title) or self.validateInput(author) or self.validateInput(copyright_year) or self.validateInput(author_birthdate)):
			self.redirect('/')
			return
		
		# 文字列からの変換が必要なものだけ変換する
		# 入力されていない場合も考えてチェックする
		year = None
		if self.validateInput(copyright_year):
			year = int(copyright_year)
		
		birthdate = None
		if self.validateInput(author_birthdate):
			st = time.strptime(author_birthdate, '%Y-%m-%d')
			birthdate = datetime.date(st.tm_year, st.tm_mon, st.tm_mday)
		
		# POSTされた情報をエンティティのプロパティに設定する
		entity = Book(
					title				= title,
					author				= author,
					copyright_year	= year,
					author_birthdate	= birthdate)
		
		# そのままデータストアに書き込む
		entity.put()
		
		# トップページにリダイレクトする
		self.redirect('/')
		
	# 入力データが正しいかチェックする
	def validateInput(self, input):
		return input != None and input != ''

application = webapp.WSGIApplication(
									[('/', MainPage), ('/register', Register)],
									debug=True)
def main():
	run_wsgi_app(application)
if __name__ == "__main__":
	main()
