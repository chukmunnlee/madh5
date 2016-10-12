/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl;

import com.acme.topnews.impl.rest.PushCommentTask;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author cmlee
 */
@WebServlet("/comment")
public class CommentServlet extends HttpServlet {	
	
	private static final Logger logger = Logger.getLogger(CommentServlet.class.getName());
	
	@Inject private PushCommentTask commentTask;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {		
		process(req, resp);		
	}		
	
	private void process(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		logger.log(Level.INFO, "Posting comment: {0}", req.getMethod());
		
		String name = req.getParameter(Constants.PARAM_NAME);
		String comment = req.getParameter(Constants.PARAM_COMMENT);
		String title = req.getParameter(Constants.PARAM_TITLE);
		String errText = null;			
		int status = HttpServletResponse.SC_ACCEPTED;
		
		if (Constants.isNullorEmpty(name)) {
			errText = "Missing name from " + req.getRemoteAddr();
			status = HttpServletResponse.SC_BAD_REQUEST;			name = "";
		}
		
		if (Constants.isNullorEmpty(comment)) {
			errText = append(errText, "No comment");
			status = HttpServletResponse.SC_BAD_REQUEST;	
		}
		
		if (Constants.isNullorEmpty(title)) {
			errText = append(errText, "No title");
			status = HttpServletResponse.SC_BAD_REQUEST;
			title = "";
		}
		
		comment = append(errText, comment);
		
		JsonObject json = Json.createObjectBuilder()
				.add(Constants.ATTR_NAME, name)
				.add(Constants.ATTR_COMMENT, comment)
				.add(Constants.ATTR_TITLE, title)
				.build();				
		logger.log(Level.INFO, "Posting comment: {0}", json);
		
		commentTask.publish(json);
		
		resp.setStatus(status);
	}
	
	private String append(String s, String text) {
		if (Constants.isNullorEmpty(s))
			return (text);
		return (s + "\n" + text);
	}
	
}
