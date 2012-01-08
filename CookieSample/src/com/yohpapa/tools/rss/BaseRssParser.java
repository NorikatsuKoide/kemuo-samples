package com.yohpapa.tools.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class BaseRssParser implements RssParser {

	protected static final String RSS = "rss";
	protected static final String CHANNEL = "channel";
	protected static final String ITEM = "item";
	protected static final String TITLE = "title";
	protected static final String LINK = "link";
	protected static final String PUB_DATE = "pubDate";
	protected static final String CATEGORY = "category";
	protected static final String DESCRIPTION = "description";

	protected final String _rssUrl;
	protected BaseRssParser(String rssUrl) {
		_rssUrl = rssUrl;
	}
	
	protected InputStream getInputStream() {
		if(_rssUrl == null)
			return null;
		
		try {
			URL url = new URL(_rssUrl);
			return url.openConnection().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
