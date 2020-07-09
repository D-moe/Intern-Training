package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/login")
public class Login extends HttpServlet {
  private UserService userService;

  @Override
  public void init() {
    userService = UserServiceFactory.getUserService();
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    
    }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    if (userService.isUserLoggedIn()) {
      System.out.println("The user is already logged in");
      System.out.println("The user name is " + userService.getCurrentUser());
      response.sendRedirect("/index.html");
      // and also return the login information for the user

    } else {
      String loginUrl = userService.createLoginURL("/index.html");
      System.out.println("The user is not logged in");
      response.sendRedirect(loginUrl);
    }
  }
}
