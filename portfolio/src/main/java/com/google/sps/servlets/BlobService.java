package com.google.sps.servlets;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * A class to provide the contents of an uploaded blob when given its
 * corresponding key.
 */
@WebServlet("/serve")
public class BlobService extends HttpServlet {
  private BlobstoreService blobstoreService =
      BlobstoreServiceFactory.getBlobstoreService();
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    BlobKey blobKey = new BlobKey(request.getParameter("blob-key"));
    blobstoreService.serve(blobKey, response);
  }
}