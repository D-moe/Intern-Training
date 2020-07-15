package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/**
 * Delete data from datastore object when called. 
 */
@WebServlet("/deletedata")
public class DeleteData extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService storeData = DatastoreServiceFactory.getDatastoreService();
    // TODO(morleyd): See if there is a better way to mass delete values, this
    // seems like a common task and there is likely some optimization to do it
    // faster
    Query toDelete = new Query("Comment");
    PreparedQuery results = storeData.prepare(toDelete);
    for (Entity entity : results.asIterable()) {
      storeData.delete(entity.getKey());
    }

    response.sendRedirect("index.html");
  }
}
