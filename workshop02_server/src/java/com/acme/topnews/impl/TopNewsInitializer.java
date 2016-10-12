/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl;

import com.acme.topnews.impl.rest.PushNewsTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author cmlee
 */
@WebListener
public class TopNewsInitializer implements ServletContextListener {
	
	private static final Logger logger = Logger.getLogger(TopNewsInitializer.class.getName());
	
	@Resource(mappedName = Constants.POOL) private ManagedScheduledExecutorService service;
	
	@Inject private BBCFeedReader feedReader;
	@Inject private PushNewsTask pushNewsTask;

	@Override
	public void contextInitialized(ServletContextEvent sce) {		
		logger.log(Level.INFO, "Starting feed reader");
		ServletContext ctx = sce.getServletContext();
		ScheduledFuture future = service.scheduleAtFixedRate(feedReader
				, Constants.START_DELAY, Constants.PERIOD_FEEDREADER, TimeUnit.SECONDS);		
		ctx.setAttribute(Constants.FUTURE_FEEDREADER, future);		
		logger.log(Level.INFO, "Starting push task");
		future = service.scheduleAtFixedRate(pushNewsTask, Constants.START_DELAY
				, Constants.PERIOD_PUSHNEWS, TimeUnit.SECONDS);
		ctx.setAttribute(Constants.FUTURE_PUSHNEWS, future);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.log(Level.INFO, "Terminating feed reader");
		ServletContext ctx = sce.getServletContext();
		ScheduledFuture future = (ScheduledFuture)ctx.getAttribute(Constants.FUTURE_FEEDREADER);
		future.cancel(true);
		logger.log(Level.INFO, "Cancelling push task");
		future = (ScheduledFuture)ctx.getAttribute(Constants.FUTURE_PUSHNEWS);
		future.cancel(true);
	}
}
