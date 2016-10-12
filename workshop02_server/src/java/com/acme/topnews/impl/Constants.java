/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl;

/**
 *
 * @author cmlee
 */
public class Constants {
	
	public static final String POOL = "concurrent/myFeedPool";
	public static final int START_DELAY = 5; //seconds
	public static final int PERIOD_FEEDREADER = 900; //seconds
	public static final int PERIOD_PUSHNEWS = 15;
	public static final String FUTURE_FEEDREADER = "feedReaderFuture";
	public static final String FUTURE_PUSHNEWS = "pushNewsFuture";
	public static final String TOP_NEWS = "http://feeds.bbci.co.uk/news/rss.xml";
	
	public static final int THUMBNAIL_WIDTH = 144;
	
	public static final String PARAM_NAME = "name";
	public static final String PARAM_COMMENT = "comment";
	public static final String PARAM_TITLE = "title";
	
	public static final String ATTR_LINK = "link";
	public static final String ATTR_TITLE = PARAM_TITLE;
	public static final String ATTR_DESCRIPTION = "description";
	public static final String ATTR_PUBDATE = "pubdate";
	public static final String ATTR_THUMBNAIL = "thumbnail";
	public static final String ATTR_NAME = PARAM_NAME;
	public static final String ATTR_COMMENT = PARAM_COMMENT;	
	public static final String ATTR_COUNT = "count";	
	
	public static boolean isNullorEmpty(final String n) {
		return ((null == n) || (n.trim().length() <= 0));
	}
}
