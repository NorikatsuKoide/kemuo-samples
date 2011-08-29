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
		self.response.out.write('''
		<html>
			<head>
				<title>The page for datastore</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
			<p class="very_important">Hello, App Engine!</p>
			<img src="/images/%s/sample.png" />
			<hr>
			<table>
			<tr>
				<td><a href="/create">Create</td>
			</tr>
			<tr>
				<td><a href="/update">Update</td>
			</tr>
			<tr>
				<td><a href="/read">Read</td>
			</tr>
			<tr>
				<td><a href="/delete">Delete</td>
			</tr>
			</table>
			</body>
		</html>
		''' % (os.environ['CURRENT_VERSION_ID'],
				os.environ['CURRENT_VERSION_ID']))

class CreatePage(webapp.RequestHandler):
	def get(self):
		self.response.headers['Content-Type'] = 'text/html'
		self.response.out.write('''
		<html>
			<head>
				<title>The page for Creating</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
			<p class="very_important">エンティティを作成します</p>
			<img src="/images/%s/sample.png" />
			<hr>
			<form action="/register" method="post">
				<table>
				<tr>
					<td class="label">key:</td>
					<td><input type="text" name="key"></td>
				</tr>
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
				<input type="submit" value="登録">
			</form>
			<hr>
			<a href="/">戻る</a>
			</body>
		</html>
		''' % (os.environ['CURRENT_VERSION_ID'],
				os.environ['CURRENT_VERSION_ID']))

class ReadPage(webapp.RequestHandler):
	def get(self):
		self.response.headers['Content-Type'] = 'text/html'
		self.response.out.write('''
		<!doctype html>
		<html lang="en">
			<head>
				<meta charset=utf-8>
				<title>The page for Reading</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
				<p class="very_important">キー名からエンティティを検索します</p>
				<img src="/images/%s/sample.png" />
				<hr>
				<form action="/read" method="post">
					<p class="little_important">キー名: </p>
					<input type="text" name="key_name">
					<input type="submit" value="検索">
				</form>
				<hr>
				<a href="/">戻る</a>
			</body>
		</html>
		''' % (os.environ['CURRENT_VERSION_ID'],
				os.environ['CURRENT_VERSION_ID']))
	
	def post(self):
		key_name = self.request.get('key_name')
		key = db.Key.from_path('Book', key_name)
		entity = Book.get(key)
		self.response.headers['Content-Type'] = 'text/html'
		body = ''
		if entity == None:
			body = '該当するエンティティがありません'
		else:
			body = '''
			<table>
				<tr>
					<td class="little_important">title:</td>
					<td>%s</td>
				</tr>
				<tr>
					<td class="little_important">author:</td>
					<td>%s</td>
				</tr>
				<tr>
					<td class="little_important">copyright_year:</td>
					<td>%s</td>
				<tr>
					<td class="little_important">author_birthdate:</td>
					<td>%s</td>
				</tr>
				</table>
			''' % (
				entity.title,
				entity.author,
				entity.copyright_year,
				entity.author_birthdate)
			
		self.response.out.write('''
		<!doctype html>
		<html lang="en">
			<head>
				<meta charset=utf-8>
				<title>The page for Reading</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
				<p class="very_important">キー名で検索した結果: %s</p>
				<img src="/images/%s/sample.png" />
				<hr>
				%s
				<hr>
				<a href="/read">戻る</a>
			</body>
		</html>
		''' % (os.environ['CURRENT_VERSION_ID'],
				key_name,
				os.environ['CURRENT_VERSION_ID'],
				body))

class Register(webapp.RequestHandler):
	def post(self):
		
		# まずはPOSTのBodyを全て取り出す
		key					= self.request.get('key')
		title				= self.request.get('title')
		author				= self.request.get('author')
		copyright_year	= self.request.get('copyright_year')
		author_birthdate	= self.request.get('author_birthdate')
		
		# キーはないと話にならないので最初にチェック
		if not self.validateInput(key):
			self.redirect('/')
			return
		
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
					key_name			= key,
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
									[('/', MainPage),
									 ('/create', CreatePage),
									 ('/read', ReadPage),
									 ('/register', Register)],
									debug=True)
def main():
	run_wsgi_app(application)
if __name__ == "__main__":
	main()
