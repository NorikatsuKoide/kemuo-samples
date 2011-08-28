#! -*- coding: utf-8 -*-

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

import os

class Book(db.Expando):
	pass

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
		title = self.request.get('title')
		author = self.request.get('author')
		copyright_year = self.request.get('copyright_year')
		author_birthdate = self.request.get('author_birthdate')
		
		entity = Book()
		if title != None:
			entity.title = title
			
		if author != None:
			entity.author = author
			
		if copyright_year != None:
			entity.copyright_year = copyright_year
			
		if author_birthdate != None:
			entity.author_birthdate = author_birthdate
			
		entity.put()
		
		self.redirect('/')

application = webapp.WSGIApplication(
									[('/', MainPage), ('/register', Register)],
									debug=True)
def main():
	run_wsgi_app(application)
if __name__ == "__main__":
	main()
