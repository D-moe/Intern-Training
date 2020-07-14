package com.google.sps.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
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
    System.out.println("The array is " + userData);
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
    String comment = request.getParameter("comment-body");
    // it is allowable for the image url to be null
    String url = getUploadedFileUrl(request, "image");
    commentEntity.setProperty("user", userName);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timeStamp);
    commentEntity.setProperty("image", url);
    storeData.put(commentEntity);
    userData.add(new Comment(userName, comment, url));
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
      // TODO(morleyd): Change implementation to use a hash to lower time
      // complexity, need to figure out how this interacts with Gson.
      // Ensure that duplicate comments (same user and commentData) are not
      // stored in the datastorage.
      String comment = entity.getProperty("comment").toString();
      String url = (String)entity.getProperty("image");
      // TODO(morleyd): Change implementation to use a hash to lower time
      // complexity, need to figure out how this interacts with Gson.
      Comment userComment = new Comment(user, comment, url);
      if (!userData.contains(userComment)) {
        userData.add(userComment);
      }
    }
  }

  private String getUploadedFileUrl(HttpServletRequest request,
                                    String formInputElementName) {
    BlobstoreService blobstoreService =
        BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a URL (dev
    // server).
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL (live
    // server).
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an
    // image file https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we
    // must use the relative path to the image, rather than the path returned by
    // imagesService which contains a host.
    String servingUrl = imagesService.getServingUrl(options);
    try {
      URL url = new URL(servingUrl);
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
