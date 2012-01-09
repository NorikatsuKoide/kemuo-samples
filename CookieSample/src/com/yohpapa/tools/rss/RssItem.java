package com.yohpapa.tools.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RssItem implements Comparable<RssItem>, Cloneable {
	private SimpleDateFormat _FORMATTER;

	private String _title = null;
	private URL _link = null;
	private Date _pubDate = null;
	private String _category = null;
	private String _description = null;
	
	public RssItem() {
		SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss zzz", Locale.US);
		formatter.setTimeZone(TimeZone.getDefault());
		_FORMATTER = formatter;
	}
	
	public String getTitle() {return _title;}
	public URL getLink() {return _link;}
	public Date getPubDate() {return _pubDate;}
	public String getCategory() {return _category;}
	public String getDescription() {return _description;}
	
	public void setTitle(String title) {
		_title = title;
	}
	
	public void setLink(String url) {
		try {
			_link = new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setPubDate(String date) {
		try {
			_pubDate = _FORMATTER.parse(date.trim());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setCategory(String category) {
		_category = category;
	}
	
	public void setDescription(String description) {
		_description = description;
	}
	
	@Override
	public int compareTo(RssItem another) {
		
		if(another.getPubDate() == null || _pubDate == null)
			return 0;
		
		return another.getPubDate().compareTo(_pubDate);
	}
	
	@Override
	protected RssItem clone() {
		try {
			return (RssItem)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void clear() {
		_title = null;
		_link = null;
		_pubDate = null;
		_category = null;
		_description = null;
	}
}
