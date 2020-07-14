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
import com.google.sps.objects.Comment;

/**
 * Store data through the datastore service using get/post requests.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  /* The userData is the current data that has been fetched from the datastore
   * service, it acts as a local cache of the data that has been recorded. Both
   * the doGet and doPost methods rely on this local cache, only refreshing if
   * the refresh parameter is specified and true.
   */
  private ArrayList<Comment> userData = new ArrayList<Comment>();
  private DatastoreService storeData;
  // Intent is for the get method to be called on the load of the comment body.
  @Override
  public void init() {
    storeData = DatastoreServiceFactory.getDatastoreService();
  }
  /**
   * Get the value of the data stored in userData, only refresh this data from
   * the database if refresh is defined and true in the request.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html;");
    String cacheValue = request.getParameter("refresh");
    boolean refreshCache = Boolean.parseBoolean(cacheValue);
    if (refreshCache) {
      System.out.println("Refresh cache");
      refreshData(request, response);
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
      System.out.println("Refresh cache");
      refreshData(request, response);
      response.sendRedirect("/index.html");
      return;
    }

    long timeStamp = System.currentTimeMillis();
    Entity commentEntity = new Entity("Comment");
    String userName = request.getParameter("user");
    String commentData = request.getParameter("comment-body");
    // Handle error checking in case invalid data is provided.
    if (userName == null || commentData == null) {
      System.err.println("An error occurred while receiving the request");
      System.err.println("The userName had a value of " + userName);
      System.err.println("The commentData had a value of " + commentData);
      response.sendRedirect("/index.html");
      return;
    }

    commentEntity.setProperty("user", userName);
    commentEntity.setProperty("comment", commentData);
    commentEntity.setProperty("timestamp", timeStamp);
    storeData.put(commentEntity);
    userData.add(new Comment(userName, commentData));
    System.out.format("The username is %s\n. The comment body is %s:", userName,
                      commentData);
    response.sendRedirect("/index.html");
    // Handle possible scripting attacks on the JavaScript side when we receive
    // the response. Should consider whether or not this issue should be handled
    // on the server side or not.
  }
  private void refreshData(HttpServletRequest request,
                           HttpServletResponse response) {
    int queryLimit;
    String requestLimit = request.getParameter("limit");

    try {
      // Convert the input to an integer.
      queryLimit = Integer.parseInt(requestLimit);
      System.out.println("Integer parsed successfully while refreshing data.");
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + requestLimit +
                         " use default");
      queryLimit = 20;
    }

    // Reset the data.
    userData.clear();
    System.out.println("The current queryLimit is " + queryLimit + ".");
    FetchOptions options = FetchOptions.Builder.withLimit(queryLimit);
    Query recentComments =
        new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> results = storeData.prepare(recentComments).asList(options);
    for (Entity entity : results) {
      System.out.println("This is running");
      String user = entity.getProperty("user").toString();
      String commentData = entity.getProperty("comment").toString();
      // TODO(morleyd): Change implementation to use a hash to lower time
      // complexity, need to figure out how this interacts with Gson.
      Comment userComment = new Comment(user, commentData);
      // Ensure that duplicate comments (same user and commentData) are not
      // stored in the datastorage.
      if (!userData.contains(userComment)) {
        userData.add(userComment);
      }
    }
  }
}