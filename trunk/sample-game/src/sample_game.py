#! -*- coding: utf-8 -*-
from google.appengine.ext import db, webapp
from google.appengine.ext.webapp.util import run_wsgi_app
import os

class Player(db.Model):
	name = db.StringProperty()
	level = db.IntegerProperty()
	score = db.IntegerProperty()
	charclass = db.StringProperty()
	create_date = db.DateTimeProperty(auto_now_add=True)
	
class MainPage(webapp.RequestHandler):
	def get(self):
		self.response.headers['Content-Type'] = 'text/html'
		self.response.out.write('''
		<html>
			<head>
				<title>sample_game</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
			<p class="very_important">Hello, App Engine!</p>
			<img src="/images/%s/sample.png" />
			<hr>
			<table>
			<tr>
				<td><a href="/register">Register</td>
			</tr>
			<tr>
				<td><a href="/sort1">Sort1</td>
			</tr>
			</table>
			</body>
		</html>
		''' % (os.environ['CURRENT_VERSION_ID'],
				os.environ['CURRENT_VERSION_ID']))

class RegisterPage(webapp.RequestHandler):
	def get(self):
		self.response.headers['Content-Type'] = 'text/html'
		body = u'''
		<!doctype html>
		<html lang="en">
			<head>
				<meta charset=utf-8>
				<title>The page for Creating</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
			<p class="very_important">エンティティを登録します</p>
			<img src="/images/%s/sample.png" />
			<hr>
			<form action="/register" method="post">
				<table>
				<tr>
					<td class="label">名前: </td>
					<td><input type="text" name="name"></td>
				</tr>
				<tr>
					<td class="label">レベル: </td>
					<td><input type="number" name="level"></td>
				</tr>
				<tr>
					<td class="label">得点: </td>
					<td><input type="number" name="score"></td>
				</tr>
				<tr>
					<td class="label">クラス: </td>
					<td><input type="text" name="class"></td>
				</tr>
				</table>
				<input type="submit" value="登録">
			</form>
			<hr>
			これまでに登録しているエンティティは
		''' % (os.environ['CURRENT_VERSION_ID'],
				os.environ['CURRENT_VERSION_ID'])
		
		#query = db.GqlQuery('SELECT * FROM Player')
		query = Player.all()
		if query:
			body += u'以下の通りです。'
			body += u'''
			<table border=1>
			<tr>
				<th class="label">名前</th>
				<th class="label">レベル</th>
				<th class="label">得点</th>
				<th class="label">クラス</th>
			<tr>'''
			
			for player in query:
				body += u'''
					<tr align=center>
						<td>%s</td>
						<td>%d</td>
						<td>%d</td>
						<td>%s</td>
					</tr>
				''' % (player.name, player.level, player.score, player.charclass)
			
			body += u'</table>'
			
		else:
			body += u'ありません。'
			
		body += u'''
			<hr>
			<a href="/">戻る</a>
			</body>
		</html>
		'''
		
		self.response.out.write(body)
	
	def post(self):
		name = self.request.get('name')
		level = self.request.get('level')
		score = self.request.get('score')
		charclass = self.request.get('class')
		
		player = Player(
					name		= name,
					level		= int(level),
					score		= int(score),
					charclass	= charclass)
		player.put()
		self.redirect('/register')

class Sort1Page(webapp.RequestHandler):
	def get(self):
		order = self.request.GET.get('order')
		if order == None:
			order = ''
		
		self.response.headers['Content-Type'] = 'text/html'
		body = u'''
		<!doctype html>
		<html lang="en">
			<head>
				<meta charset=utf-8>
				<title>The page for Creating</title>
				<link rel="stylesheet" href="/css/%s/sample.css" type="text/css" />
			</head>
			<body>
				<p class="very_important">エンティティをlevelとscoreでソートします</p>
				<img src="/images/%s/sample.png" />
				<hr>
		''' % (os.environ['CURRENT_VERSION_ID'],
				os.environ['CURRENT_VERSION_ID'])

		query = db.GqlQuery('''
		SELECT * FROM Player ORDER BY level %s, score %s
		''' % (order, order))
		
		body += u'''
				<table border=1>
					<tr>
						<th class="label">名前</th>
						<th class="label">レベル</th>
						<th class="label">得点</th>
						<th class="label">クラス</th>
					<tr>
		'''
		
		for player in query:
			body += u'''
					<tr align=center>
						<td>%s</td>
						<td>%d</td>
						<td>%d</td>
						<td>%s</td>
					</tr>
			''' % (player.name, player.level, player.score, player.charclass)
			
		asc = ''
		desc = ''
		if order == '':
			asc = 'checked'
		else:
			desc = 'checked'
		
		body += u'''
				</table>
				<p>
				<form method="post" action="/sort1">
					<input type="radio" name="order" value="asc" %s>昇順
					<input type="radio" name="order" value="desc" %s>降順
					<input type="submit" value="ソートする">
				</form>
				<hr>
				<a href="/">戻る</a>
			</body>
		</html>
		''' % (asc, desc)
		
		self.response.out.write(body)
		
	def post(self):
		url = '/sort1'
		order = self.request.get('order')
		if order == 'desc':
			url += '?order=desc'
			
		self.redirect(url)
			
application = webapp.WSGIApplication(
					[('/', MainPage),
					 ('/register', RegisterPage),
					 ('/sort1', Sort1Page)],
					debug=True)

def main():
	run_wsgi_app(application)
if __name__ == "__main__":
	main()