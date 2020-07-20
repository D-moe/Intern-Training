package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Logout the user from their account. 
 */
@WebServlet("/logout")
public class Logout extends HttpServlet {
  private UserService userService;

  @Override
  public void init() {
    userService = UserServiceFactory.getUserService();
  }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String logoutUrl = userService.createLogoutURL("/index.html");
    response.sendRedirect(logoutUrl);
  }
}
