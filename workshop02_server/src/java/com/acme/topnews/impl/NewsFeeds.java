/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.json.JsonObject;

/**
 *
 * @author cmlee
 */
@Singleton
@Lock(LockType.READ)
public class NewsFeeds {
    
    private static final Logger logger = Logger.getLogger(NewsFeeds.class.getName());

    private List<JsonObject> newsFeeds = new LinkedList<>();
    private long timestamp = -System.currentTimeMillis();

    @Lock(LockType.WRITE)
    public void refresh(final List<JsonObject> fresh) {
        logger.log(Level.INFO, "Refreshing news feeds: {0}", fresh.size());
        newsFeeds = fresh;
        timestamp = System.currentTimeMillis();
    }

    @Lock(LockType.READ)
    public JsonObject get(final int idx) {
        if ((idx < 0) || (idx >= newsFeeds.size()))
            return (null);
        return (newsFeeds.get(idx));
    }

    @Lock(LockType.READ)
    public long getTimestamp() {
        return (timestamp);
    }

    @Lock(LockType.READ)
    public JsonObject get() {
        return (get(0));
    }

    @Lock(LockType.READ)
    public int size() {
        return (newsFeeds.size());
    }
}
