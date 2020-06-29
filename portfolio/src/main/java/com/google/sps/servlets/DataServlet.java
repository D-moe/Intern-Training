package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
/**
 * Servlet that returns some example content. TODO(morleyd): modify this file
 * to handle comments data
 */

@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<String> data = new ArrayList<>(Arrays.asList(
      "Nice shirt", "that is interesting", "what a crappy website"));

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html;");
    Gson gson = new Gson();
    String json = gson.toJson(data);
    response.getWriter().println(json);
  }
}
