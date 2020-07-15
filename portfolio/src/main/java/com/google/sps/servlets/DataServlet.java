package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.objects.User;

/**
 * A class to fetch data from the datastore library.
 * @param userData The current data that has been fetched from the datastore
 *     library, it acts as a local cache of the data that has been recorded.
 *     Both the doGet and doPost methods rely on this local cache, only
 *     refreshing if the refresh parameter is specified and true.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<User> userData = new ArrayList<User>();
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
    System.out.println("The array is " + userData);
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
    // TODO(morleyd): Add error checking in case some of the expected headers
    // are not included in the request, etc.

    long timeStamp = System.currentTimeMillis();
    Entity commentEntity = new Entity("Comment");
    String userName = request.getParameter("user");
    String comment = request.getParameter("comment-body");
    // it is allowable for the image url to be null
    BlobKey currentKey = getBlobKey(request, "image");
    String keyString = null;
    if (currentKey != null) {
      keyString = currentKey.getKeyString();
    }
    commentEntity.setProperty("user", userName);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timeStamp);
    commentEntity.setProperty("image", keyString);
    storeData.put(commentEntity);
    userData.add(new User(userName, comment, keyString));
    System.out.format("The username is %s\n. The comment body is %s:", userName,
                      comment);
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
      System.out.println("parse successful");
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + requestLimit +
                         " use default");
      queryLimit = 20;
    }

    // Reset the data.
    userData.clear();
    System.out.println(queryLimit);
    FetchOptions options = FetchOptions.Builder.withLimit(queryLimit);
    Query recentComments =
        new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> results = storeData.prepare(recentComments).asList(options);
    for (Entity entity : results) {
      System.out.println("This is running");
      String user = entity.getProperty("user").toString();
      String comment = entity.getProperty("comment").toString();
      String blobKey = (String)entity.getProperty("image");
      // TODO(morleyd): Change implementation to use a hash to lower time
      // complexity, need to figure out how this interacts with Gson.
      User userComment = new User(user, comment, blobKey);
      if (!userData.contains(userComment)) {
        userData.add(userComment);
      }
    }
  }

  private BlobKey getBlobKey(HttpServletRequest request,
                             String formInputElementName) {
    BlobstoreService blobstoreService =
        BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev
    // server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);
    return blobKey;
  }
}