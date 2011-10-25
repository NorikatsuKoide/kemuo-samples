#! -*- coding: utf-8 -*-
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.api import mail

# バグレポートAPIハンドラ
class BugReportHandler(webapp.RequestHandler):
	def get(self):
		self.get_or_post()
	
	def post(self):
		self.get_or_post()
	
	# GET/POSTメソッド (共通)
	def get_or_post(self):
		
		# 各種パラメータ抽出
		device		= self.request.get("dev")
		model		= self.request.get("mod")
		sdk			= self.request.get("sdk")
		version	= self.request.get("ver")
		exception	= self.request.get("exc")
		
		# メール本文作成
		content = u'''
		dev: %s
		mod: %s
		sdk: %s
		ver: %s
		exc:
		%s
		''' % (device, model, sdk, version, exception)
		
		# ログメールを開発者に送信する
		mail.send_mail(
				sender="hogehoge@gmail.com",
				to="hogehoge@gmail.com",
				subject="hogehoge",
				body=content)
		
		# 一応何かしらのレスポンスを返す
		self.response.out.write('Success!')


application = webapp.WSGIApplication(
					[('/bug', BugReportHandler)],
					debug=True)

def main():
	run_wsgi_app(application)
	
if __name__ == "__main__":
	main().request.get("mod")
	