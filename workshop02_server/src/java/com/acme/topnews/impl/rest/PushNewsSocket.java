package com.acme.topnews.impl.rest;

import java.io.IOException;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@RequestScoped
@ServerEndpoint("/event")
public class PushNewsSocket {

	private static Logger logger = Logger.getLogger(PushNewsSocket.class.getName());

	@Inject private SocketMap socketMap;

	@OnOpen
	public void open(Session session) {

		logger.info(String.format("Open connection: %1$s", session.getId()));

		try {
			socketMap.writeLock(() -> {
				socketMap.add(session);
				logger.info(String.format("Connection count: %1$d", socketMap.size()));
			});
		} catch(Throwable ex) {
			logger.warning(String.format("@OnOpen exception: id=%1$s, msg=%1$s", session.getId(), ex.getMessage()));
		}

	}

	@OnClose
	public void close(Session session) {

		logger.info(String.format("Close connection: %1$s", session.getId()));

		try {
			socketMap.writeLock(() -> {
				socketMap.remove(session);
				logger.info(String.format("Connection count: %1$d", socketMap.size()));
			});
		} catch (Throwable ex) {
			logger.warning(String.format("@OnClose exception: id=%1$s, msg=%1$s", session.getId(), ex.getMessage()));
		}

	}
}
