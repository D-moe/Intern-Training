package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.objects.User;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<User> userData = new ArrayList<User>();
  private DatastoreService storeData;
  // Intent is for the get method to be called on the load of the comment body.
  @Override
  public void init() {
    storeData = DatastoreServiceFactory.getDatastoreService();
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html;");
    // Now query the data store to load data, unlike before and get the values
    // in order of the newest comment.
    int queryLimit;

    String requestLimit = request.getParameter("limit");

    // Convert the input to an integer.
    try {
      queryLimit = Integer.parseInt(requestLimit);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + requestLimit +
                         " use default");
      queryLimit = 20;
    }

    FetchOptions options = FetchOptions.Builder.withLimit(queryLimit);
    Query recentComments =
        new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> results = storeData.prepare(recentComments).asList(options);
    for (Entity entity : results) {
      System.out.println("This is running");
      String user = entity.getProperty("user").toString();
      String comment = entity.getProperty("comment").toString();
      // TODO(morleyd): Change implementation to use a hash to lower time
      // complexity, need to figure out how this interacts with Gson.
      User userComment = new User(user, comment);
      if (!userData.contains(userComment)) {
        userData.add(userComment);
      }
    }
    // The additional parameters used here ensure that the
    // generated JSON looks much nicer to parse in the browser.
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(userData);
    System.out.format("Printing out the following JSON: %s", json);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html");

    boolean refreshCache;
    String cacheValue = request.getParameter("refresh");
    refreshCache = Boolean.parseBoolean(cacheValue);
    if (refreshCache) {
      userData.clear();
      response.sendRedirect("/index.html");
      return;
    }
    // TODO(morleyd): Add error checking in case some of the expected headers
    // are not included in the request, etc.

    long timeStamp = System.currentTimeMillis();
    Entity commentEntity = new Entity("Comment");
    String userName = request.getParameter("user");
    String comment = request.getParameter("comment-body");
    commentEntity.setProperty("user", userName);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timeStamp);
    storeData.put(commentEntity);
    userData.add(new User(userName, comment));
    System.out.format("The username is %s\n. The comment body is %s:", userName,
                      comment);
    response.sendRedirect("/index.html");
    // Handle possible scripting attacks on the JavaScript side when we receive
    // the response. Should consider whether or not this issue should be handled
    // on the server side or not.
  }
}
