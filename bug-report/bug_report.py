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
		address1	= self.request.get("adr1")
		address2	= self.request.get("adr2")
		address3	= self.request.get("adr3")
		
		if address1 == "":
			self.response.out.write('Failed')
			return
		
		address = [address1]
		if address2 != "":
			address.append(address2)
			
		if address3 != "":
			address.append(address3)
		
		# メール本文作成
		content = u'''
Build.DEVICE: %s
Build.MODEL: %s
Build.VERSION.SDK: %s
PackageInfo.versionName: %s
Exception stack trace:
%s
		''' % (device, model, sdk, version, exception)
		
		# ログメールを開発者に送信する
		mail.send_mail(
				sender="hogehoge@gmail.com",
				to=address,
				subject="Hogehoge Bug Report",
				body=content)
		
		# 一応何かしらのレスポンスを返す
		self.response.out.write('Success!')


application = webapp.WSGIApplication(
					[('/bug', BugReportHandler)],
					debug=True)

def main():
	run_wsgi_app(application)
	
if __name__ == "__main__":
	main()
	