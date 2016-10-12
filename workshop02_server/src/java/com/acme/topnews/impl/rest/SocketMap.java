package com.acme.topnews.impl.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.websocket.CloseReason;
import javax.websocket.Session;

@ApplicationScoped
public class SocketMap {

	private static final Logger logger = Logger.getLogger(SocketMap.class.getName());

	private final Map<String, Session> sockets = new HashMap<>();
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

	public void readLock(final Runnable r) throws Throwable {
		final Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
			r.run();
		} finally {
			readLock.unlock();
		}
	}

	public void writeLock(final Runnable r) throws Throwable {
		final Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		try {
			r.run();
		} finally {
			writeLock.unlock();
		}
	}

	public Integer size() {
		return (sockets.size());
	}

	public void add(final Session session) {
		sockets.put(session.getId(), session);
	}
	public void remove(final Session session) {
		sockets.remove(session.getId());
	}

	public void broadcast(final JsonObject json) {
		List<String> removeList = new LinkedList<>();
		final String msg = json.toString();

		for (Session s: sockets.values()) 
			try {
				s.getBasicRemote().sendText(msg);
			} catch (IOException ex) {
				removeList.add(s.getId());
			}
		removeList.forEach(id -> { 
			try {
				sockets.remove(id).close();
			} catch (IOException ex) { }
		});
	}

	public void closeAll() {
		sockets.values().stream()
				.forEach(s -> {
					try { s.close(); } catch (IOException ex) { }
				});
	}
}
