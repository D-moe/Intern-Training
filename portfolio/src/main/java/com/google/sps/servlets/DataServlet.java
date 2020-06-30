package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.objects.User;
/**
 * Servlet that returns some example content. TODO(morleyd): modify this file
 * to handle comments data
 */

@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<User> userData = new ArrayList<User>();

  // Intent is for the get method to be called on the load of the comment body.
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html;");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(userData);
    System.out.format("Printting out the following JSON: %s", json);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html");
    // The additional parameters used here ensure that the generated JSON looks
    // much nicer to parse in the browser.
    String userName = request.getParameter("user");
    String comment = request.getParameter("comment-body");
    userData.add(new User(userName, comment));
    System.out.format("The username is %s\n. The comment body is %s:", userName,
                      comment);
    response.sendRedirect("/index.html");
    // Handle possible scripting attacks on the JavaScript side when we receive
    // the response. Should consider whether or not this issue should be handled
    // on the server side or not.
  }
}
