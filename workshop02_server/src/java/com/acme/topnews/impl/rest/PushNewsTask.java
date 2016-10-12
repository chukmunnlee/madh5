/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl.rest;

import com.acme.topnews.impl.NewsFeeds;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author cmlee
 */
@ApplicationScoped
public class PushNewsTask implements Runnable {

    private final Logger logger = Logger.getLogger(PushNewsTask.class.getName());

    @EJB private NewsFeeds newsFeeds;
	@Inject SocketMap socketMap;

    private final Lock lock = new ReentrantLock();
    
    private long timestamp = 0L;
    private int idx = 0;
    private int count = 0;

    @PreDestroy
    private void cleanUp() {
		try {
			socketMap.writeLock(() -> { 
				socketMap.closeAll(); 
			});
		} catch (Throwable t) { }
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Pushing news: {0}", LocalDateTime.now());

        lock.lock();
        try {

            long ts = newsFeeds.getTimestamp();
            if (timestamp != ts) {
                timestamp = ts;
                idx = 0;
                count = newsFeeds.size();
            }
            JsonObject news = newsFeeds.get(idx);
            if (null == news)
                return;
			socketMap.readLock(() -> { 
				socketMap.broadcast(Json.createObjectBuilder()
						.add("type", "news")
						.add("payload", news)
						.build());
			});
            idx = ++idx % count;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
