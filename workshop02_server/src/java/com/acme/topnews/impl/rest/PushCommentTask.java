/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl.rest;

import com.acme.topnews.impl.Constants;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author cmlee
 */
@ApplicationScoped
public class PushCommentTask {

    private final Logger logger = Logger.getLogger(PushNewsTask.class.getName());

	@Resource(lookup = Constants.POOL)
	private ManagedScheduledExecutorService service;

	@Inject private SocketMap socketMap;

    private class PushTask implements Runnable {

        private final JsonObject comment;

        public PushTask(JsonObject comment) {
            this.comment = comment;
        }

        @Override
        public void run() {
			try {
				socketMap.readLock(() -> { 
					socketMap.broadcast(Json.createObjectBuilder()
							.add("type", "comment")
							.add("payload", comment)
							.build());
				});
			} catch (Throwable t) { 
				logger.warning(String.format("Exception pushing comment: %1$s", t.getMessage()));
			}
        }
    }

    public void publish(final JsonObject comment) {
        service.execute(new PushTask(comment));
    }
}
