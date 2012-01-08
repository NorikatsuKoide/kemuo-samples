package com.yohpapa.tools.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class WebHistoryParser extends BaseRssParser {

	private String _cookie = null;
	
	public WebHistoryParser(String rssUrl) {
		super(rssUrl);
	}
	
	public void setCookie(String cookie) {
		_cookie = cookie;
	}
	
	@Override
	protected InputStream getInputStream() {
		if(_rssUrl == null)
			return null;
		
		// CookieをHTTPクライアントに設定する
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter("http.useragent", "Mozilla/5.0");
		if(_cookie != null) {
			String[] cookies = _cookie.split(";");
			for(String value : cookies) {
				value = value.trim();
				String[] cookieSet = value.split("=");
				BasicClientCookie bCookie = new BasicClientCookie(cookieSet[0], cookieSet[1]);
				bCookie.setDomain("google.com");
				bCookie.setPath("/");
				CookieStore store = client.getCookieStore();
				store.addCookie(bCookie);
			}
		}
		
		// HTTPリクエストを発行して
		// RSSを読み込むためのInputStreamを返却する
		try {
			HttpGet request = new HttpGet(URI.create(_rssUrl));
			HttpResponse response = client.execute(request);
			
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return null;
			
			HttpEntity entity = response.getEntity();
			return entity.getContent();
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {}
	}

	@Override
	public List<RssItem> parse() {
		final RssItem rssItem = new RssItem();
		final List<RssItem> rssItems = new ArrayList<RssItem>();
		
		// SAX用XML要素生成
		RootElement root = new RootElement(RSS);
		Element channel = root.getChild(CHANNEL);
		Element item = channel.getChild(ITEM);
		
		// 各種SAXハンドラ設定
		
		item.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				rssItems.add(rssItem.clone());
				rssItem.clear();
			}
		});
		
		item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				rssItem.setTitle(body);
			}
		});
		
		item.getChild(LINK).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				rssItem.setLink(body);
			}
		});
		
		item.getChild(PUB_DATE).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				rssItem.setPubDate(body);
			}
		});
		
		item.getChild(CATEGORY).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				rssItem.setCategory(body);
			}
		});
		
		item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				rssItem.setDescription(body);
			}
		});
		
		InputStream stream = null;
		try {
			// RSS取得用ストリームを取得してXMLをパースする
			stream = getInputStream();
			Xml.parse(stream, Xml.Encoding.UTF_8, root.getContentHandler());
			stream = null;
			return rssItems;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
