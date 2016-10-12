/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme.topnews.impl.rest;

import com.acme.topnews.impl.Constants;
import com.acme.topnews.impl.NewsFeeds;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author cmlee
 */

@RequestScoped
@Path("/news")
public class PushNewsResource {
	
	@Inject private PushNewsTask task;
	@EJB private NewsFeeds newsFeeds;
	
	@GET
	@Path("count")
	@Produces(MediaType.APPLICATION_JSON)
	public Response newsItemCount() {
		JsonObject result = Json.createObjectBuilder()
				.add(Constants.ATTR_COUNT, newsFeeds.size())
				.build();
		return (Response.ok(result).build());
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		JsonObject newsItem = newsFeeds.get(id);
		if (null == newsItem)
			return (Response.status(Response.Status.NOT_ACCEPTABLE)
					.entity("Invalid news id: " + id).build());
		return (Response.ok(newsItem).build());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() {
		return (get(0));
	}
}
