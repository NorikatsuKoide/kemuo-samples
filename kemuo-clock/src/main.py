from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
import datetime
import models
import logging

class MainPage(webapp.RequestHandler):
	def get(self):
		logging.debug('Host: %s' % self.request.headers['Host']);
		
		time = datetime.datetime.now()
		user = users.get_current_user()

		if not user:
			navbar = ('<p class="little_important">Welcome! <a href="%s">Sign in or register</a> to customize.</p>') % (users.create_login_url(self.request.path))
			tz_form = ''
		else:
			userprefs = models.get_userprefs()
			navbar = ('<p class="little_important>Welcome! %s! You can <a href="%s">sign out</a>.</p>') % (user.email(), users.create_logout_url(self.request.path))
			tz_form = '''
				<form action="/prefs" method="post">
					<lable for="tz_offset">
						Timezon offset from UTC (can be negative):
					</lable>
					<input name="tz_offset" id="tz_offset" type="text" size="4" value="%d" />
					<input type="submit" value="Set" />
				</form>
			''' % userprefs.tz_offset
			time += datetime.timedelta(0, 0, 0, 0, 0, userprefs.tz_offset)

		self.response.headers['Content-Type'] = 'text/html'
		self.response.out.write('''
		<html>
			<head>
				<title>The Time Is...</title>
				<link rel="stylesheet" href="/css/sample.css" type="text/css" />
			</head>
			<body>
			<img src="/images/sample.png" />
			%s
				<p class="very_important">The time is: %s</p>
			%s
			</body>
		</html>
		''' % (navbar, str(time), tz_form))

application = webapp.WSGIApplication([('/', MainPage)], debug = True)

def main():
	run_wsgi_app(application)

if __name__ == '__main__':
	main()