/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl;

import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import com.sun.syndication.feed.module.mediarss.types.Thumbnail;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import java.math.BigDecimal;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author cmlee
 */
@ApplicationScoped
public class BBCFeedReader implements Runnable {

    private static final Logger logger = Logger.getLogger(BBCFeedReader.class.getName());

    @EJB private NewsFeeds newsFeeds;

    @Override
    public void run() {
        logger.log(Level.INFO, "Start reading feed: {0}", ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        List<JsonObject> feedList = new LinkedList<>();
        try {
            final FeedFetcher fetcher = new HttpURLFeedFetcher();
            final SyndFeed feed = fetcher.retrieveFeed(new URL(Constants.TOP_NEWS));
            for (Object f : feed.getEntries()) {
                final SyndEntry entry = (SyndEntry) f;
                final String pubDate = ZonedDateTime.ofInstant(
                        entry.getPublishedDate().toInstant(), ZoneId.systemDefault())
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME);
                final JsonObjectBuilder builder = Json.createObjectBuilder();
                builder.add(Constants.ATTR_LINK, entry.getLink())
                        .add(Constants.ATTR_TITLE, entry.getTitle())
                        .add(Constants.ATTR_DESCRIPTION, entry.getDescription().getValue())
                        .add(Constants.ATTR_PUBDATE, pubDate);
                MediaEntryModule media = (MediaEntryModule) entry.getModule(MediaEntryModule.URI);
                if (null != media)
                    for (Thumbnail tn : media.getMetadata().getThumbnail()) {
                        if (tn.getWidth() >= Constants.THUMBNAIL_WIDTH) {
                            builder.add(Constants.ATTR_THUMBNAIL, tn.getUrl().toString());
                            break;
                        }
                    }
                final JsonObject j = builder.build();
                logger.log(Level.INFO, "BBC: {0}", j.toString());
                feedList.add(j);
            }
            newsFeeds.refresh(feedList);
            logger.log(Level.INFO, "Feed size: {0}", feedList.size());
        } catch (Throwable ex) {
            logger.log(Level.WARNING, "Reading feed", ex);
        }
        logger.log(Level.INFO, "Completed reading feed: {0}", ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }
}
