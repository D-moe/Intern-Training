package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that returns some example content. TODO(morleyd): modify this file
 * to handle comments data
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Hello David Morley!</h1>");
  }
  /*
   * We now need to modify this file to properly handle JSON
   * This shouldn't be too difficult
   */
}

@WebServlet("/json")
public class JsonServlet extends HttpServlet {
  private ArrayList<String> data = new ArrayList<>(Arrays.asList(
      "Nice shirt", "that's interesting", "what a crappy website"));

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html;");
    Gson gson = new Gson();
    String json = gson.toJson(data);
    response.getWriter().println(json);
  }
  /*
   * We now need to modify this file to properly handle JSON
   * This shouldn't be too difficult
   */
}

