package com.google.sps.servlets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A class to provide the contents of an uploaded blob when given its
 * corresponding key.
 */
@WebServlet("/favorite")
public class FavoritePlaces extends HttpServlet {
  private List<Places> favoritePlaces;
  @Override
  public void init() {
    favoritePlaces = new ArrayList<Places>(Arrays.asList(
        new Places(34.071577, -118.450043, "Favorite Dorm"),
        new Places(34.070413, -118.446897, "Bball!"),
        new Places(34.072678, 34.072678, "Lunch Everyday"),
        new Places(34.075193, -118.440100, "So Pretty"),
        new Places(34.066797, -118.441694, "Great for Meditation")));
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(favoritePlaces);
    System.out.format("Printing out the following JSON: %s", json);
    response.getWriter().println(json);
  }
}

// 34.071577, -118.450043 Sproul Landing
// 34.070413, -118.446897 Pauley Pavilion
// 34.072678, -118.450335 Bruin Cafe
// 34.075193, -118.440100 Sculpture Garden
// 34.066797, -118.441694 Botanical Gardens